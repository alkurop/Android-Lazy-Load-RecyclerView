package com.github.alkurop.updatinglist.mock_classes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.github.alkurop.updatinglist.R;

/**
 * Created by alkurop on 01.07.16.
 */
public class TestActivity extends AppCompatActivity{
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat);
        super.onCreate(savedInstanceState);


    }
}
