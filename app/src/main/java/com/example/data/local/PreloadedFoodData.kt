package com.example.data.local

import com.example.data.local.entity.FoodItem
import com.example.data.local.entity.ReminderSetting

object PreloadedFoodData {

    fun getDefaultFoodItems(): List<FoodItem> = listOf(
        // --- Pakistani Desi Specialties ---
        FoodItem(
            name = "Chicken Biryani",
            urduName = "چکن بریانی",
            category = "Pakistani Desi",
            defaultServing = "1 plate (350g)",
            caloriesPerServing = 580,
            proteinPerServing = 28.0f,
            carbsPerServing = 76.0f,
            fatsPerServing = 18.0f,
            isDesi = true,
            weightGainTip = "High-carb and protein combo; pair with cucumber raita for extra hydration and digestion."
        ),
        FoodItem(
            name = "Beef Biryani (Nalli/Dum)",
            urduName = "بیف بریانی",
            category = "Pakistani Desi",
            defaultServing = "1 plate (350g)",
            caloriesPerServing = 640,
            proteinPerServing = 34.0f,
            carbsPerServing = 74.0f,
            fatsPerServing = 23.0f,
            isDesi = true,
            weightGainTip = "Calorie-dense red meat provides creatine and iron, ideal for muscle bulking."
        ),
        FoodItem(
            name = "Chicken Karahi (Desi Style)",
            urduName = "چکن کڑاہی",
            category = "Pakistani Desi",
            defaultServing = "1 bowl (250g)",
            caloriesPerServing = 460,
            proteinPerServing = 40.0f,
            carbsPerServing = 6.0f,
            fatsPerServing = 30.0f,
            isDesi = true,
            weightGainTip = "High protein and healthy fats. Eat with 2 rotis or 1 roghani naan."
        ),
        FoodItem(
            name = "Beef Nihari",
            urduName = "نہاری",
            category = "Pakistani Desi",
            defaultServing = "1 deep bowl (300g)",
            caloriesPerServing = 570,
            proteinPerServing = 42.0f,
            carbsPerServing = 12.0f,
            fatsPerServing = 38.0f,
            isDesi = true,
            weightGainTip = "Slow-cooked shank beef loaded with collagen, protein, and energy for lean mass."
        ),
        FoodItem(
            name = "Haleem (Beef & 7 Grains)",
            urduName = "حلیم",
            category = "Pakistani Desi",
            defaultServing = "1 bowl (300g)",
            caloriesPerServing = 430,
            proteinPerServing = 32.0f,
            carbsPerServing = 48.0f,
            fatsPerServing = 13.0f,
            isDesi = true,
            weightGainTip = "Ultimate Pakistani weight-gain superfood: slow-cooked lentils, whole wheat, and shredded beef."
        ),
        FoodItem(
            name = "Beef Qeema Mutter",
            urduName = "بیف قیمہ مٹر",
            category = "Pakistani Desi",
            defaultServing = "1 bowl (200g)",
            caloriesPerServing = 390,
            proteinPerServing = 32.0f,
            carbsPerServing = 12.0f,
            fatsPerServing = 24.0f,
            isDesi = true,
            weightGainTip = "Minced beef cooked with aromatic spices; easy to consume in surplus calories."
        ),
        FoodItem(
            name = "Mutton Paya Soup",
            urduName = "مٹن پائے",
            category = "Pakistani Desi",
            defaultServing = "1 bowl with shorba",
            caloriesPerServing = 390,
            proteinPerServing = 36.0f,
            carbsPerServing = 4.0f,
            fatsPerServing = 26.0f,
            isDesi = true,
            weightGainTip = "Rich in bone marrow, amino acids, and gelatin for joint support during heavy workouts."
        ),
        FoodItem(
            name = "Daal Chawal (Tarka)",
            urduName = "دال چاول",
            category = "Pakistani Desi",
            defaultServing = "1 large plate",
            caloriesPerServing = 470,
            proteinPerServing = 17.0f,
            carbsPerServing = 82.0f,
            fatsPerServing = 10.0f,
            isDesi = true,
            weightGainTip = "Complete plant-protein profile when rice and lentils combine. Add a spoonful of desi ghee!"
        ),
        FoodItem(
            name = "Daal Mash with Desi Ghee",
            urduName = "دال ماش فرائی",
            category = "Pakistani Desi",
            defaultServing = "1 bowl (200g)",
            caloriesPerServing = 310,
            proteinPerServing = 16.0f,
            carbsPerServing = 34.0f,
            fatsPerServing = 14.0f,
            isDesi = true,
            weightGainTip = "Urad lentils have high branched-chain amino acids (BCAAs) that promote muscle synthesis."
        ),
        FoodItem(
            name = "Chicken Tikka Leg Piece",
            urduName = "چکن تکہ لیگ",
            category = "Pakistani Desi",
            defaultServing = "1 whole quarter piece",
            caloriesPerServing = 280,
            proteinPerServing = 36.0f,
            carbsPerServing = 2.0f,
            fatsPerServing = 14.0f,
            isDesi = true,
            weightGainTip = "Lean muscle fuel! Eat with paratha to quickly hit your surplus target."
        ),
        FoodItem(
            name = "Shami Kabab (Beef)",
            urduName = "شامی کباب",
            category = "Pakistani Desi",
            defaultServing = "2 pieces (120g)",
            caloriesPerServing = 260,
            proteinPerServing = 22.0f,
            carbsPerServing = 10.0f,
            fatsPerServing = 14.0f,
            isDesi = true,
            weightGainTip = "Convenient high-protein snack between meals."
        ),
        FoodItem(
            name = "Seekh Kabab (Beef)",
            urduName = "سیخ کباب",
            category = "Pakistani Desi",
            defaultServing = "2 skewers (140g)",
            caloriesPerServing = 310,
            proteinPerServing = 30.0f,
            carbsPerServing = 4.0f,
            fatsPerServing = 19.0f,
            isDesi = true,
            weightGainTip = "Juicy protein-packed dinner staple."
        ),

        // --- Roti, Paratha & Breads ---
        FoodItem(
            name = "Roti / Chapati (Whole Wheat Atta)",
            urduName = "گندم کی روٹی",
            category = "Grains & Roti",
            defaultServing = "1 medium roti (60g)",
            caloriesPerServing = 120,
            proteinPerServing = 3.6f,
            carbsPerServing = 25.0f,
            fatsPerServing = 0.8f,
            isDesi = true,
            weightGainTip = "Complex carbs for sustained workout energy."
        ),
        FoodItem(
            name = "Paratha (Crispy Desi Ghee)",
            urduName = "دیسی گھی کا پراٹھا",
            category = "Grains & Roti",
            defaultServing = "1 medium paratha",
            caloriesPerServing = 290,
            proteinPerServing = 5.2f,
            carbsPerServing = 36.0f,
            fatsPerServing = 15.0f,
            isDesi = true,
            weightGainTip = "Classic Pakistani breakfast bulking fuel with saturated fats and high energy density."
        ),
        FoodItem(
            name = "Aloo Paratha (Stuffed)",
            urduName = "آلو والا پراٹھا",
            category = "Grains & Roti",
            defaultServing = "1 large paratha",
            caloriesPerServing = 340,
            proteinPerServing = 6.0f,
            carbsPerServing = 46.0f,
            fatsPerServing = 16.0f,
            isDesi = true,
            weightGainTip = "Carb-dense breakfast; dip in full-fat dahi for an extra 100 calories."
        ),
        FoodItem(
            name = "Roghani Naan with Sesame",
            urduName = "روغنی نان",
            category = "Grains & Roti",
            defaultServing = "1 whole naan (120g)",
            caloriesPerServing = 330,
            proteinPerServing = 8.5f,
            carbsPerServing = 56.0f,
            fatsPerServing = 8.0f,
            isDesi = true,
            weightGainTip = "Baked with milk and ghee, perfect partner for Nihari and Karahi."
        ),

        // --- Dairy, Shakes & Chai ---
        FoodItem(
            name = "Full Cream Buffalo Milk",
            urduName = "بھینس کا خالص دودھ",
            category = "Dairy & Shakes",
            defaultServing = "1 large glass (250ml)",
            caloriesPerServing = 230,
            proteinPerServing = 10.0f,
            carbsPerServing = 13.0f,
            fatsPerServing = 16.0f,
            isDesi = true,
            weightGainTip = "Pakistani buffalo milk has double the butterfat of cow milk; effortless liquid calories!"
        ),
        FoodItem(
            name = "Desi Banana Almond Shake",
            urduName = "کیلا بادام شیک",
            category = "Dairy & Shakes",
            defaultServing = "1 large mug (350ml)",
            caloriesPerServing = 460,
            proteinPerServing = 15.0f,
            carbsPerServing = 68.0f,
            fatsPerServing = 16.0f,
            isDesi = true,
            weightGainTip = "2 bananas + whole milk + 10 soaked almonds + 1 spoon honey. The ultimate home gainer shake."
        ),
        FoodItem(
            name = "Doodh Pati Chai (Cardamom & Sugar)",
            urduName = "کڑک دودھ پتی چائے",
            category = "Dairy & Shakes",
            defaultServing = "1 cup (180ml)",
            caloriesPerServing = 170,
            proteinPerServing = 5.5f,
            carbsPerServing = 19.0f,
            fatsPerServing = 8.0f,
            isDesi = true,
            weightGainTip = "Brewed entirely in milk instead of water. Great 150+ kcal boost after meals."
        ),
        FoodItem(
            name = "Meethi Lassi (Full Cream)",
            urduName = "میٹھی ملائی والی لسی",
            category = "Dairy & Shakes",
            defaultServing = "1 large glass (300ml)",
            caloriesPerServing = 310,
            proteinPerServing = 9.0f,
            carbsPerServing = 42.0f,
            fatsPerServing = 12.0f,
            isDesi = true,
            weightGainTip = "Refreshing probiotic beverage packed with calcium and natural calories."
        ),

        // --- Street Food, Snacks & Sweets ---
        FoodItem(
            name = "Aloo Samosa",
            urduName = "آلو سموسہ",
            category = "Street Food",
            defaultServing = "1 crispy piece (90g)",
            caloriesPerServing = 220,
            proteinPerServing = 4.5f,
            carbsPerServing = 26.0f,
            fatsPerServing = 12.0f,
            isDesi = true,
            weightGainTip = "Quick savory snack for afternoon tea time."
        ),
        FoodItem(
            name = "Mixed Pakoras",
            urduName = "مکس پکوڑے",
            category = "Street Food",
            defaultServing = "1 plate (120g)",
            caloriesPerServing = 340,
            proteinPerServing = 8.0f,
            carbsPerServing = 30.0f,
            fatsPerServing = 21.0f,
            isDesi = true,
            weightGainTip = "Besan (gram flour) fritters give plant protein and caloric surplus."
        ),
        FoodItem(
            name = "Gulab Jamun (Warm in Sheera)",
            urduName = "گلاب جامن",
            category = "Street Food",
            defaultServing = "2 pieces (100g)",
            caloriesPerServing = 320,
            proteinPerServing = 4.0f,
            carbsPerServing = 52.0f,
            fatsPerServing = 11.0f,
            isDesi = true,
            weightGainTip = "High glycemic carbs to replenish depleted glycogen after intense training."
        ),
        FoodItem(
            name = "Khoya Kheer",
            urduName = "کھویا کھیر",
            category = "Street Food",
            defaultServing = "1 small bowl (150g)",
            caloriesPerServing = 310,
            proteinPerServing = 8.0f,
            carbsPerServing = 45.0f,
            fatsPerServing = 11.0f,
            isDesi = true,
            weightGainTip = "Slow-reduced milk solids and rice; rich dessert for evening calories."
        ),

        // --- Western & Universal Muscle Staples ---
        FoodItem(
            name = "Boiled Eggs (Large)",
            urduName = "ابلے ہوئے انڈے",
            category = "Protein Sources",
            defaultServing = "2 eggs (100g)",
            caloriesPerServing = 144,
            proteinPerServing = 12.6f,
            carbsPerServing = 0.8f,
            fatsPerServing = 9.8f,
            isDesi = false,
            weightGainTip = "Gold-standard bioavailability protein with all essential amino acids."
        ),
        FoodItem(
            name = "Fried Eggs in Butter / Ghee",
            urduName = "فرائی انڈے دیسی گھی میں",
            category = "Protein Sources",
            defaultServing = "2 eggs sunny-side up",
            caloriesPerServing = 215,
            proteinPerServing = 13.0f,
            carbsPerServing = 1.0f,
            fatsPerServing = 18.0f,
            isDesi = false,
            weightGainTip = "Boosts calories effortlessly in the morning."
        ),
        FoodItem(
            name = "Chicken Breast (Cooked)",
            urduName = "چکن بریسٹ",
            category = "Protein Sources",
            defaultServing = "150g portion",
            caloriesPerServing = 240,
            proteinPerServing = 46.0f,
            carbsPerServing = 0.0f,
            fatsPerServing = 5.0f,
            isDesi = false,
            weightGainTip = "Pure lean protein to ensure surplus goes to muscle rather than only fat."
        ),
        FoodItem(
            name = "Peanut Butter (Natural Creamy)",
            urduName = "پینٹ بٹر",
            category = "Protein Sources",
            defaultServing = "2 tablespoons (32g)",
            caloriesPerServing = 190,
            proteinPerServing = 8.0f,
            carbsPerServing = 7.0f,
            fatsPerServing = 16.0f,
            isDesi = false,
            weightGainTip = "Densely packed monounsaturated fats. Spread on toast or blend into shakes."
        ),
        FoodItem(
            name = "Rolled Oats with Milk & Honey",
            urduName = "دودھ اور شہد کے ساتھ جو",
            category = "Breakfast",
            defaultServing = "1 large bowl (250g)",
            caloriesPerServing = 380,
            proteinPerServing = 15.0f,
            carbsPerServing = 64.0f,
            fatsPerServing = 8.0f,
            isDesi = false,
            weightGainTip = "Beta-glucan fibers improve gut health and nutrient absorption."
        ),
        FoodItem(
            name = "Bananas",
            urduName = "کیلے",
            category = "Breakfast",
            defaultServing = "2 medium bananas (240g)",
            caloriesPerServing = 210,
            proteinPerServing = 2.6f,
            carbsPerServing = 54.0f,
            fatsPerServing = 0.6f,
            isDesi = false,
            weightGainTip = "Easy to digest, high potassium prevents muscle cramping during gym workouts."
        ),
        FoodItem(
            name = "Mixed Almonds & Walnuts",
            urduName = "بادام اور اخروٹ",
            category = "Protein Sources",
            defaultServing = "Handful (35g)",
            caloriesPerServing = 210,
            proteinPerServing = 7.0f,
            carbsPerServing = 5.0f,
            fatsPerServing = 19.0f,
            isDesi = false,
            weightGainTip = "Keep in your pocket for snacking throughout the day."
        ),
        FoodItem(
            name = "Pakistani Medjool / Aseel Dates",
            urduName = "اصیل کھجور",
            category = "Breakfast",
            defaultServing = "4 soft dates (80g)",
            caloriesPerServing = 230,
            proteinPerServing = 2.0f,
            carbsPerServing = 60.0f,
            fatsPerServing = 0.3f,
            isDesi = true,
            weightGainTip = "Natural energy booster; eat 4 dates with a glass of milk before sleep."
        ),
        FoodItem(
            name = "Steamed White Basmati Rice",
            urduName = "باسمتی چاول",
            category = "Grains & Roti",
            defaultServing = "1 plate (200g cooked)",
            caloriesPerServing = 260,
            proteinPerServing = 5.0f,
            carbsPerServing = 58.0f,
            fatsPerServing = 0.6f,
            isDesi = true,
            weightGainTip = "Digestible starch that doesn't leave you feeling overly bloated."
        )
    )

    fun getDefaultReminders(): List<ReminderSetting> = listOf(
        ReminderSetting(
            id = "breakfast",
            title = "Morning Desi Breakfast",
            time = "08:30",
            isEnabled = true,
            message = "Start your day strong! Have your paratha, eggs or high-calorie shake."
        ),
        ReminderSetting(
            id = "water_morning",
            title = "Morning Hydration",
            time = "11:00",
            isEnabled = true,
            message = "Time for 500ml water to aid your calorie absorption!"
        ),
        ReminderSetting(
            id = "lunch",
            title = "Weight-Gain Lunch",
            time = "13:30",
            isEnabled = true,
            message = "Biryani, Karahi, or Daal Chawal time! Log your midday nutrition."
        ),
        ReminderSetting(
            id = "water_afternoon",
            title = "Afternoon Water",
            time = "16:30",
            isEnabled = true,
            message = "Stay hydrated: have another 500ml before your evening snack."
        ),
        ReminderSetting(
            id = "snack_evening",
            title = "Shaam Ka Snack / Chai",
            time = "17:30",
            isEnabled = true,
            message = "High calorie snack time! Banana shake, samosa, or doodh pati + nuts."
        ),
        ReminderSetting(
            id = "dinner",
            title = "Muscle Recovery Dinner",
            time = "20:30",
            isEnabled = true,
            message = "High-protein dinner to fuel overnight muscle repair!"
        ),
        ReminderSetting(
            id = "bedtime",
            title = "Bedtime Milk & Sleep Reminder",
            time = "23:00",
            isEnabled = true,
            message = "Glass of warm milk with dates, and aim for 8 hours of restorative sleep."
        )
    )
}
