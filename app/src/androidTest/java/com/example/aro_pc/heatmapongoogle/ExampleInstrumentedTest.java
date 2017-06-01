package com.example.aro_pc.heatmapongoogle;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.xamarin.testcloud.espresso.Factory;
import com.xamarin.testcloud.espresso.ReportHelper;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.aro_pc.heatmapongoogle", appContext.getPackageName());
    }

    @Rule
    public ReportHelper reportHelper = Factory.getReportHelper();

    @After
    public void TearDown(){
        reportHelper.label("Stopping App");
    }


    // xtc test app.apk e55a41240f0668dd29adf63b4e7ffd68 --devices 56167b4f --series "master" --user adana942@mail.ru --workspace app/build/outputs/apk
//    D:\Android\Projects\HeatMap\HeatMapOnGoogle\xtc\xtc.exe
}

