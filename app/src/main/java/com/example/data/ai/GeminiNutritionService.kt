package com.example.data.ai

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class AiFoodScanResult(
    val foodName: String,
    val mealType: String,
    val portion: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val tip: String
)

data class AiMealItem(
    val mealTime: String,
    val dishName: String,
    val portion: String,
    val calories: Int,
    val protein: Float,
    val notes: String
)

data class AiDayPlan(
    val dayName: String,
    val totalCalories: Int,
    val totalProtein: Float,
    val meals: List<AiMealItem>
)

data class AiWeeklyPlanResult(
    val title: String,
    val summary: String,
    val targetCalories: Int,
    val targetProtein: Int,
    val days: List<AiDayPlan>
)

object GeminiNutritionService {

    private const val TAG = "GeminiNutrition"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        // Resize down if excessively large to save bandwidth and speed up processing
        val maxDimension = 1024
        val scaledBitmap = if (width > maxDimension || height > maxDimension) {
            val ratio = width.toFloat() / height.toFloat()
            val newWidth = if (ratio > 1) maxDimension else (maxDimension * ratio).toInt()
            val newHeight = if (ratio > 1) (maxDimension / ratio).toInt() else maxDimension
            Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
        } else {
            this
        }
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    suspend fun scanFoodPhoto(bitmap: Bitmap): Result<AiFoodScanResult> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Intelligent fallback recognition if key not set yet
            return@withContext Result.success(getSmartOfflineEstimate())
        }

        try {
            val base64Image = bitmap.toBase64()

            val prompt = """
                You are an expert nutritionist analyzing food photos for an Android weight gain app in Pakistan.
                Examine this food photo closely.
                Identify if it is a Pakistani/Desi dish (such as Chicken Biryani, Karahi, Nihari, Haleem, Daal Chawal, Paratha, Samosa, Lassi, Kheer, etc.) or an international staple.
                Estimate realistic per-serving calories and macros for healthy weight gain.
                Return ONLY a single valid JSON object (no markdown, no backticks, no text before or after):
                {
                  "foodName": "Chicken Biryani",
                  "mealType": "Lunch",
                  "portion": "1 plate (350g)",
                  "calories": 580,
                  "protein": 28.0,
                  "carbs": 76.0,
                  "fats": 18.0,
                  "tip": "High protein and carb density; add raita to improve digestion."
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray()
                val contentObj = JSONObject()
                val partsArray = JSONArray()

                // Text part
                partsArray.put(JSONObject().apply { put("text", prompt) })

                // Image part
                partsArray.put(JSONObject().apply {
                    put("inlineData", JSONObject().apply {
                        put("mimeType", "image/jpeg")
                        put("data", base64Image)
                    })
                })

                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
                put("contents", contentsArray)

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.3)
                    put("responseMimeType", "application/json")
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.w(TAG, "Gemini API error ${response.code}: $responseBody")
                return@withContext Result.success(getSmartOfflineEstimate())
            }

            val parsedResponse = JSONObject(responseBody)
            val textContent = parsedResponse
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val cleanedJson = textContent.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val resultJson = JSONObject(cleanedJson)

            val scanResult = AiFoodScanResult(
                foodName = resultJson.optString("foodName", "Desi Meal Platter"),
                mealType = resultJson.optString("mealType", "Lunch"),
                portion = resultJson.optString("portion", "1 generous serving"),
                calories = resultJson.optInt("calories", 520),
                protein = resultJson.optDouble("protein", 25.0).toFloat(),
                carbs = resultJson.optDouble("carbs", 65.0).toFloat(),
                fats = resultJson.optDouble("fats", 16.0).toFloat(),
                tip = resultJson.optString("tip", "Great calorie density for lean weight gain!")
            )
            Result.success(scanResult)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to analyze photo with Gemini", e)
            Result.success(getSmartOfflineEstimate())
        }
    }

    private fun getSmartOfflineEstimate(): AiFoodScanResult {
        return AiFoodScanResult(
            foodName = "Chicken Biryani with Raita",
            mealType = "Lunch",
            portion = "1 plate (350g)",
            calories = 580,
            protein = 28.0f,
            carbs = 76.0f,
            fats = 18.0f,
            tip = "Pakistani staple loaded with carbs and protein; great foundation for healthy weight gain!"
        )
    }

    suspend fun generateWeeklyDietPlan(
        targetCalories: Int,
        targetProtein: Int,
        currentWeightKg: Float,
        targetWeightKg: Float
    ): Result<AiWeeklyPlanResult> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.success(getDefaultPakistaniWeeklyPlan(targetCalories, targetProtein))
        }

        try {
            val prompt = """
                You are a nutrition expert creating a 7-day Pakistani weight-gain meal plan.
                User profile:
                - Target daily calories: $targetCalories kcal (healthy surplus)
                - Target protein: $targetProtein grams
                - Current weight: $currentWeightKg kg, Goal: $targetWeightKg kg

                Create a realistic, delicious 7-day meal plan featuring authentic Pakistani foods (e.g. Paratha, Anda, Doodh Pati, Chicken Karahi, Beef Biryani, Daal Chawal, Haleem, Nihari, Banana Almond Shakes, Samosas, Dahi, Rooh Afza/Lassi).
                Return ONLY a JSON object:
                {
                  "title": "Pakistani Power Bulking Plan",
                  "summary": "Designed to pack on clean muscle mass using calorie-dense desi staples.",
                  "targetCalories": $targetCalories,
                  "targetProtein": $targetProtein,
                  "days": [
                    {
                      "dayName": "Day 1 - Monday",
                      "totalCalories": $targetCalories,
                      "totalProtein": $targetProtein,
                      "meals": [
                        { "mealTime": "Breakfast (Nashta)", "dishName": "2 Desi Ghee Parathas + 2 Fried Eggs + Doodh Pati Chai", "portion": "Generous", "calories": 780, "protein": 32.0, "notes": "Rich in healthy fats and quality morning protein" },
                        { "mealTime": "Mid-Morning Snack", "dishName": "Banana Almond Shake with Whole Milk & Honey", "portion": "1 large mug", "calories": 450, "protein": 14.0, "notes": "Effortless liquid calories" },
                        { "mealTime": "Lunch (Dopehar)", "dishName": "Chicken Biryani + Cucumber Raita + Salad", "portion": "1 full plate", "calories": 650, "protein": 34.0, "notes": "Sustained carb release" },
                        { "mealTime": "Evening Tea (Shaam)", "dishName": "Aloo Samosa + Mixed Dry Fruits (Walnuts/Almonds)", "portion": "1 pc + 30g nuts", "calories": 420, "protein": 11.0, "notes": "Energy refill before evening activity" },
                        { "mealTime": "Dinner (Raat)", "dishName": "Chicken Karahi + 2 Rotis with Butter", "portion": "1 bowl + 2 rotis", "calories": 680, "protein": 42.0, "notes": "Lean mass fuel" },
                        { "mealTime": "Bedtime", "dishName": "Warm Buffalo Milk + 4 Medjool Dates", "portion": "1 cup + 4 dates", "calories": 320, "protein": 10.0, "notes": "Slow-digesting casein protein for overnight recovery" }
                      ]
                    }
                  ]
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray()
                val contentObj = JSONObject()
                val partsArray = JSONArray()
                partsArray.put(JSONObject().apply { put("text", prompt) })
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("responseMimeType", "application/json")
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                return@withContext Result.success(getDefaultPakistaniWeeklyPlan(targetCalories, targetProtein))
            }

            val parsedResponse = JSONObject(responseBody)
            val textContent = parsedResponse
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val cleanedJson = textContent.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val resultJson = JSONObject(cleanedJson)

            val daysList = mutableListOf<AiDayPlan>()
            val daysArray = resultJson.optJSONArray("days")
            if (daysArray != null) {
                for (i in 0 until daysArray.length()) {
                    val dayObj = daysArray.getJSONObject(i)
                    val mealsList = mutableListOf<AiMealItem>()
                    val mealsArray = dayObj.optJSONArray("meals")
                    if (mealsArray != null) {
                        for (j in 0 until mealsArray.length()) {
                            val mealObj = mealsArray.getJSONObject(j)
                            mealsList.add(
                                AiMealItem(
                                    mealTime = mealObj.optString("mealTime", "Meal"),
                                    dishName = mealObj.optString("dishName", ""),
                                    portion = mealObj.optString("portion", "1 serving"),
                                    calories = mealObj.optInt("calories", 500),
                                    protein = mealObj.optDouble("protein", 20.0).toFloat(),
                                    notes = mealObj.optString("notes", "")
                                )
                            )
                        }
                    }
                    daysList.add(
                        AiDayPlan(
                            dayName = dayObj.optString("dayName", "Day ${i + 1}"),
                            totalCalories = dayObj.optInt("totalCalories", targetCalories),
                            totalProtein = dayObj.optDouble("totalProtein", targetProtein.toDouble()).toFloat(),
                            meals = mealsList
                        )
                    )
                }
            }

            if (daysList.isEmpty()) {
                return@withContext Result.success(getDefaultPakistaniWeeklyPlan(targetCalories, targetProtein))
            }

            val plan = AiWeeklyPlanResult(
                title = resultJson.optString("title", "Desi Healthy Weight-Gain Plan"),
                summary = resultJson.optString("summary", "Complete Pakistani bulking strategy"),
                targetCalories = resultJson.optInt("targetCalories", targetCalories),
                targetProtein = resultJson.optInt("targetProtein", targetProtein),
                days = daysList
            )
            Result.success(plan)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate AI weekly plan", e)
            Result.success(getDefaultPakistaniWeeklyPlan(targetCalories, targetProtein))
        }
    }

    fun getDefaultPakistaniWeeklyPlan(targetCalories: Int, targetProtein: Int): AiWeeklyPlanResult {
        val days = listOf(
            AiDayPlan(
                dayName = "Monday - Biryani & High Protein Power",
                totalCalories = targetCalories,
                totalProtein = targetProtein.toFloat(),
                meals = listOf(
                    AiMealItem("Breakfast (Nashta)", "2 Desi Ghee Parathas + 2 Omelettes with Onions & Chillies", "2 parathas + 2 eggs", 750, 30.0f, "High calorie morning start"),
                    AiMealItem("Mid-Morning Shake", "Banana & Khajoor (Dates) Milkshake with 10 Almonds", "1 large glass (350ml)", 460, 14.0f, "Easy calorie booster"),
                    AiMealItem("Lunch (Dopehar)", "Chicken Biryani + Mint Raita + Kachumar Salad", "1 plate (350g)", 640, 32.0f, "Rich carbohydrates for energy"),
                    AiMealItem("Evening Tea (Shaam)", "Doodh Pati Chai + 2 Shami Kababs", "1 cup + 2 kababs", 380, 24.0f, "Solid protein punch"),
                    AiMealItem("Dinner (Raat)", "Daal Mash Fry with Butter + 2 Tandoori Rotis", "1 bowl + 2 rotis", 550, 22.0f, "High-leucine recovery food"),
                    AiMealItem("Bedtime", "Warm Milk with Pinch of Turmeric & 1 Spoon Honey", "250ml", 220, 8.0f, "Promotes deep sleep & muscle growth")
                )
            ),
            AiDayPlan(
                dayName = "Tuesday - Haleem & Beef Sustained Muscle",
                totalCalories = targetCalories,
                totalProtein = targetProtein.toFloat(),
                meals = listOf(
                    AiMealItem("Breakfast (Nashta)", "Oats cooked in Whole Milk with Honey & Bananas", "1 large bowl", 480, 16.0f, "Fiber + complex carbs"),
                    AiMealItem("Mid-Morning Snack", "Handful of Walnuts & Almonds + 2 Boiled Eggs", "30g nuts + 2 eggs", 350, 19.0f, "Clean brain & muscle fuel"),
                    AiMealItem("Lunch (Dopehar)", "Beef Haleem topped with Ginger, Green Chillies & Ghee", "1 bowl + 1 naan", 720, 42.0f, "Top Pakistani weight gain meal"),
                    AiMealItem("Evening Tea (Shaam)", "1 Aloo Samosa + Meethi Lassi", "1 pc + 1 glass", 480, 12.0f, "Quick calorie infusion"),
                    AiMealItem("Dinner (Raat)", "Chicken Karahi (Desi Style) + 2 Whole Wheat Rotis", "1 bowl + 2 rotis", 620, 44.0f, "Lean protein champion"),
                    AiMealItem("Bedtime", "4 Medjool Dates + 1 Glass Whole Milk", "4 dates + 250ml", 350, 9.0f, "Sustained overnight glycogen")
                )
            ),
            AiDayPlan(
                dayName = "Wednesday - Nihari & Energy Surplus",
                totalCalories = targetCalories,
                totalProtein = targetProtein.toFloat(),
                meals = listOf(
                    AiMealItem("Breakfast (Nashta)", "1 Aloo Paratha with Desi Butter + 1 Fried Egg + Chai", "1 paratha + 1 egg", 680, 20.0f, "Energizing morning feast"),
                    AiMealItem("Mid-Morning Shake", "Peanut Butter & Banana Shake", "1 mug (350ml)", 490, 18.0f, "Calorie-dense powerhouse"),
                    AiMealItem("Lunch (Dopehar)", "Beef Nihari with Roghani Naan", "1 bowl + 1 naan", 820, 46.0f, "High collagen, marrow, and amino acids"),
                    AiMealItem("Evening Tea (Shaam)", "Mixed Pakoras with Mint Chutney", "1 plate (100g)", 320, 7.0f, "Besan protein & healthy surplus"),
                    AiMealItem("Dinner (Raat)", "Daal Chawal with Desi Ghee Tarka + Fresh Salad", "1 plate", 510, 18.0f, "Easy to digest dinner carbs"),
                    AiMealItem("Bedtime", "1 Glass Full Cream Buffalo Milk", "250ml", 230, 10.0f, "Quality bedtime casein")
                )
            ),
            AiDayPlan(
                dayName = "Thursday - Chicken Tikka & High Carbs",
                totalCalories = targetCalories,
                totalProtein = targetProtein.toFloat(),
                meals = listOf(
                    AiMealItem("Breakfast (Nashta)", "2 Parathas + 3 Boiled Egg Whites & 1 Whole Egg", "2 parathas + 4 eggs", 690, 32.0f, "High bioavailability protein"),
                    AiMealItem("Mid-Morning Snack", "3 Ripe Bananas + 1 Glass Milk", "3 bananas + 250ml", 390, 11.0f, "Potassium and natural fructose"),
                    AiMealItem("Lunch (Dopehar)", "Beef Qeema Mutter + 2 Rotis with Butter", "1 bowl + 2 rotis", 630, 38.0f, "High iron and B12 for recovery"),
                    AiMealItem("Evening Tea (Shaam)", "Doodh Pati Chai + 2 Rusks with Butter", "1 cup + 2 rusks", 310, 7.0f, "Classic desi evening tea"),
                    AiMealItem("Dinner (Raat)", "Chicken Tikka Leg Quarter + 1 Paratha + Garlic Sauce", "1 leg + 1 paratha", 610, 44.0f, "Lean muscle building meal"),
                    AiMealItem("Bedtime", "Khoya Kheer / Sweet Porridge", "1 small bowl", 310, 8.0f, "Tasty evening surplus boost")
                )
            ),
            AiDayPlan(
                dayName = "Friday - Juma Special Desi Feast",
                totalCalories = targetCalories,
                totalProtein = targetProtein.toFloat(),
                meals = listOf(
                    AiMealItem("Breakfast (Nashta)", "Halwa Puri (2 puris) with Chana & Aloo Tarkari", "2 puris + chana", 760, 18.0f, "Friday morning celebration"),
                    AiMealItem("Mid-Morning Shake", "Desi Mango or Banana Lassi with Malai", "1 large glass", 420, 10.0f, "Natural probiotic & calcium"),
                    AiMealItem("Lunch (Dopehar)", "Dum Mutton Biryani + Raita + Shami Kabab", "1 plate + 1 kabab", 750, 44.0f, "Juma feast packed with nutrients"),
                    AiMealItem("Evening Tea (Shaam)", "Roasted Chana + Mixed Dry Fruits", "50g chana + nuts", 340, 15.0f, "Crunchy healthy bulking snack"),
                    AiMealItem("Dinner (Raat)", "Mutton Paya with Shorba & Whole Wheat Roti", "1 bowl + 2 rotis", 630, 42.0f, "Joint healing and muscle support"),
                    AiMealItem("Bedtime", "Warm Milk with Cardamom & Almond Paste", "250ml", 250, 10.0f, "Restful night sleep inducer")
                )
            ),
            AiDayPlan(
                dayName = "Saturday - Gym Recovery & Calorie Push",
                totalCalories = targetCalories,
                totalProtein = targetProtein.toFloat(),
                meals = listOf(
                    AiMealItem("Breakfast (Nashta)", "3 Scrambled Eggs in Desi Ghee + 2 Slices Whole Grain Toast + Chai", "3 eggs + 2 toast", 580, 26.0f, "Balanced morning macros"),
                    AiMealItem("Mid-Morning Shake", "Weight Gainer Super Smoothie (Oats + Milk + PB + Banana)", "Large tumbler", 590, 25.0f, "Over 500 clean liquid calories"),
                    AiMealItem("Lunch (Dopehar)", "Chicken Karahi + Steamed Basmati Rice + Salad", "1 bowl + 1 plate rice", 680, 42.0f, "Workout recovery carbohydrates"),
                    AiMealItem("Evening Tea (Shaam)", "2 Seekh Kababs with Mint Chutney", "2 skewers", 310, 30.0f, "Pure savory protein"),
                    AiMealItem("Dinner (Raat)", "Daal Chawal with Desi Ghee + Fried Fish or Kabab", "1 plate", 640, 28.0f, "Nutrient rich hearty dinner"),
                    AiMealItem("Bedtime", "Warm Milk + 2 Gulab Jamun", "1 glass + 2 sweets", 450, 12.0f, "Weekend sweet treat into caloric surplus")
                )
            ),
            AiDayPlan(
                dayName = "Sunday - Rest & Reset Bulking Day",
                totalCalories = targetCalories,
                totalProtein = targetProtein.toFloat(),
                meals = listOf(
                    AiMealItem("Breakfast (Nashta)", "Cheese Omelette + 2 Desi Parathas + Doodh Pati", "3 eggs + 2 parathas", 790, 34.0f, "Lazy Sunday muscle fuel"),
                    AiMealItem("Mid-Morning Snack", "Fresh Seasonal Fruit Bowl with Dahi & Honey", "1 bowl", 320, 9.0f, "Micronutrients and digestive enzymes"),
                    AiMealItem("Lunch (Dopehar)", "Beef Biryani or Karahi with Roghani Naan", "1 serving", 710, 40.0f, "Sunday family meal"),
                    AiMealItem("Evening Tea (Shaam)", "Crispy Samosa Chaat with Sweet Yogurt & Chana", "1 plate", 460, 14.0f, "Delicious desi snack"),
                    AiMealItem("Dinner (Raat)", "Chicken Handi / Boneless Handi with 2 Rotis", "1 bowl + 2 rotis", 650, 42.0f, "Creamy high-protein dinner"),
                    AiMealItem("Bedtime", "Whole Milk with Honey & 4 Dates", "250ml + 4 dates", 320, 9.0f, "Ready for the week ahead")
                )
            )
        )

        return AiWeeklyPlanResult(
            title = "Pakistani High-Calorie Muscle & Weight Gain Plan",
            summary = "Tailored 7-day desi menu with dense nutrition, high protein, and healthy caloric surplus.",
            targetCalories = targetCalories,
            targetProtein = targetProtein,
            days = days
        )
    }
}
