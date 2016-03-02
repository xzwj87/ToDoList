package com.github.xzwj87.todolist.schedule.data.provider;


import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase  {
    public static final String LOG_TAG = TestUriMatcher.class.getSimpleName();

    private static final long TEST_SCHEDULE_ID = 9527L;

    private static final Uri TEST_SCHEDULE_DIR = ScheduleContract.ScheduleEntry.CONTENT_URI;
    private static final Uri TEST_SCHEDULE_WITH_ID = ScheduleContract.ScheduleEntry
            .buildScheduleUri(TEST_SCHEDULE_ID);

    public void testUriMatcher() {
        UriMatcher testMatcher = ScheduleProvider.buildUriMatcher();

        assertEquals("Error: The SCHEDULE URI was matched incorrectly.",
                testMatcher.match(TEST_SCHEDULE_DIR), ScheduleProvider.SCHEDULE);

        assertEquals("Error: The SCHEDULE_WITH_ID URI was matched incorrectly.",
                testMatcher.match(TEST_SCHEDULE_WITH_ID), ScheduleProvider.SCHEDULE_WITH_ID);

    }
}
