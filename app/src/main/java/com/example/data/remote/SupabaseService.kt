package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class SupabaseAuthResult(
    val success: Boolean,
    val accessToken: String? = null,
    val userId: String? = null,
    val email: String? = null,
    val errorMessage: String? = null
)

class SupabaseService {

    companion object {
        val SUPABASE_SQL_SCHEMA = """
-- ==========================================
-- FITNESS OS SUPABASE DATABASE SCHEMA (AUTH LINKED)
-- ==========================================

-- 1. Profiles Table referencing auth.users
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT UNIQUE NOT NULL,
    full_name TEXT,
    username TEXT,
    age INT,
    gender TEXT,
    height_cm INT,
    weight_kg INT,
    avatar_url TEXT,
    goal TEXT,
    calorie_goal INT DEFAULT 2200,
    water_goal_liters NUMERIC DEFAULT 3.0,
    water_drank_liters NUMERIC DEFAULT 1.5,
    step_goal INT DEFAULT 10000,
    current_steps INT DEFAULT 0,
    calorie_burn_goal INT DEFAULT 500,
    calories_burned_today INT DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())
);

-- Automatic Profile Creation Trigger on Sign Up
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.profiles (id, email, full_name)
  VALUES (
    NEW.id,
    NEW.email,
    COALESCE(NEW.raw_user_meta_data->>'full_name', SPLIT_PART(NEW.email, '@', 1))
  )
  ON CONFLICT (id) DO NOTHING;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- 2. Workouts Table
CREATE TABLE IF NOT EXISTS public.workouts (
    id BIGSERIAL PRIMARY KEY,
    user_email TEXT NOT NULL,
    title TEXT NOT NULL,
    duration_minutes INT NOT NULL,
    exercise_count INT DEFAULT 0,
    exercises_text TEXT,
    status TEXT DEFAULT 'Completed',
    date_text TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())
);

-- 3. Meals Table
CREATE TABLE IF NOT EXISTS public.meals (
    id BIGSERIAL PRIMARY KEY,
    user_email TEXT NOT NULL,
    meal_type TEXT NOT NULL,
    food_name TEXT NOT NULL,
    calories INT NOT NULL,
    protein_grams INT DEFAULT 0,
    carbs_grams INT DEFAULT 0,
    fat_grams INT DEFAULT 0,
    image_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())
);

-- 4. Habits Table
CREATE TABLE IF NOT EXISTS public.habits (
    id BIGSERIAL PRIMARY KEY,
    user_email TEXT NOT NULL,
    title TEXT NOT NULL,
    is_completed BOOLEAN DEFAULT false,
    category TEXT DEFAULT 'General',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())
);

-- 5. Weight Logs Table
CREATE TABLE IF NOT EXISTS public.weight_logs (
    id BIGSERIAL PRIMARY KEY,
    user_email TEXT NOT NULL,
    weight_kg NUMERIC NOT NULL,
    date_text TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())
);

-- Row Level Security & Policies
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.workouts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.meals ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.habits ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.weight_logs ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow select profiles" ON public.profiles FOR SELECT USING (true);
CREATE POLICY "Allow insert profiles" ON public.profiles FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow update profiles" ON public.profiles FOR UPDATE USING (true);

CREATE POLICY "Allow public select workouts" ON public.workouts FOR SELECT USING (true);
CREATE POLICY "Allow public insert workouts" ON public.workouts FOR INSERT WITH CHECK (true);

CREATE POLICY "Allow public select meals" ON public.meals FOR SELECT USING (true);
CREATE POLICY "Allow public insert meals" ON public.meals FOR INSERT WITH CHECK (true);

CREATE POLICY "Allow public select habits" ON public.habits FOR SELECT USING (true);
CREATE POLICY "Allow public insert habits" ON public.habits FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow public update habits" ON public.habits FOR UPDATE USING (true);

CREATE POLICY "Allow public select weight" ON public.weight_logs FOR SELECT USING (true);
CREATE POLICY "Allow public insert weight" ON public.weight_logs FOR INSERT WITH CHECK (true);
        """.trimIndent()
    }

    fun isConfigured(supabaseUrl: String, anonKey: String): Boolean {
        return supabaseUrl.isNotBlank() &&
                !supabaseUrl.contains("your-supabase-project") &&
                anonKey.isNotBlank() &&
                anonKey != "your_supabase_anon_key"
    }

    suspend fun signUp(email: String, password: String, supabaseUrl: String, anonKey: String): SupabaseAuthResult = withContext(Dispatchers.IO) {
        if (!isConfigured(supabaseUrl, anonKey)) {
            return@withContext SupabaseAuthResult(
                success = false,
                errorMessage = "Supabase URL & Anon Key not configured in Secrets."
            )
        }

        try {
            val endpoint = "${supabaseUrl.trimEnd('/')}/auth/v1/signup"
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.doInput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("apikey", anonKey)

            val jsonBody = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(jsonBody.toString())
            writer.flush()
            writer.close()

            val code = conn.responseCode
            val inputStream = if (code in 200..299) conn.inputStream else conn.errorStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            if (code in 200..299) {
                val json = JSONObject(response.toString())
                val accessToken = json.optString("access_token", null)
                val user = json.optJSONObject("user")
                val userId = user?.optString("id")
                val userEmail = user?.optString("email")

                SupabaseAuthResult(
                    success = true,
                    accessToken = accessToken,
                    userId = userId,
                    email = userEmail ?: email
                )
            } else {
                val json = JSONObject(response.toString())
                val msg = json.optString("msg", json.optString("error_description", "Sign up failed"))
                SupabaseAuthResult(success = false, errorMessage = msg)
            }
        } catch (e: Exception) {
            SupabaseAuthResult(success = false, errorMessage = e.localizedMessage ?: "Supabase error")
        }
    }

    suspend fun signIn(email: String, password: String, supabaseUrl: String, anonKey: String): SupabaseAuthResult = withContext(Dispatchers.IO) {
        if (!isConfigured(supabaseUrl, anonKey)) {
            return@withContext SupabaseAuthResult(
                success = false,
                errorMessage = "Supabase URL & Anon Key not configured in Secrets."
            )
        }

        try {
            val endpoint = "${supabaseUrl.trimEnd('/')}/auth/v1/token?grant_type=password"
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.doInput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("apikey", anonKey)

            val jsonBody = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(jsonBody.toString())
            writer.flush()
            writer.close()

            val code = conn.responseCode
            val inputStream = if (code in 200..299) conn.inputStream else conn.errorStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            if (code in 200..299) {
                val json = JSONObject(response.toString())
                val accessToken = json.optString("access_token")
                val user = json.optJSONObject("user")
                val userId = user?.optString("id")
                val userEmail = user?.optString("email")

                SupabaseAuthResult(
                    success = true,
                    accessToken = accessToken,
                    userId = userId,
                    email = userEmail ?: email
                )
            } else {
                val json = JSONObject(response.toString())
                val msg = json.optString("error_description", json.optString("msg", "Invalid credentials"))
                SupabaseAuthResult(success = false, errorMessage = msg)
            }
        } catch (e: Exception) {
            SupabaseAuthResult(success = false, errorMessage = e.localizedMessage ?: "Supabase error")
        }
    }

    suspend fun postWorkoutToSupabase(
        email: String,
        title: String,
        durationMinutes: Int,
        exerciseCount: Int,
        exercisesText: String,
        status: String,
        supabaseUrl: String,
        anonKey: String
    ): Boolean = withContext(Dispatchers.IO) {
        if (!isConfigured(supabaseUrl, anonKey)) return@withContext false
        try {
            val endpoint = "${supabaseUrl.trimEnd('/')}/rest/v1/workouts"
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("apikey", anonKey)
            conn.setRequestProperty("Authorization", "Bearer $anonKey")

            val jsonBody = JSONObject().apply {
                put("user_email", email)
                put("title", title)
                put("duration_minutes", durationMinutes)
                put("exercise_count", exerciseCount)
                put("exercises_text", exercisesText)
                put("status", status)
                put("date_text", "Today")
            }

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(jsonBody.toString())
            writer.flush()
            writer.close()

            conn.responseCode in 200..299
        } catch (e: Exception) {
            false
        }
    }
}
