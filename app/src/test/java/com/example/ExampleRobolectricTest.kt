package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PermanentTask
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TribeFit", appName)
  }

  @Test
  fun `verify exactly 10 permanent tasks are defined`() {
    val tasks = PermanentTask.ALL_PERMANENT_TASKS
    assertEquals(10, tasks.size)
    assertNotNull(PermanentTask.getById(PermanentTask.ID_STEPS))
    assertNotNull(PermanentTask.getById(PermanentTask.ID_WATER))
    assertNotNull(PermanentTask.getById(PermanentTask.ID_NO_JUNK_FOOD))
    assertNotNull(PermanentTask.getById(PermanentTask.ID_PROTEIN_MEALS))
    assertNotNull(PermanentTask.getById(PermanentTask.ID_SLEEP))
    assertNotNull(PermanentTask.getById(PermanentTask.ID_WORKOUT))
    assertNotNull(PermanentTask.getById(PermanentTask.ID_STUDY))
    assertNotNull(PermanentTask.getById(PermanentTask.ID_READING))
    assertNotNull(PermanentTask.getById(PermanentTask.ID_BEDTIME))
    assertNotNull(PermanentTask.getById(PermanentTask.ID_WAKEUP))
  }
}
