package com.ua.ricardomartins.qualar.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.ua.ricardomartins.qualar.Common;
import com.ua.ricardomartins.qualar.R;
import com.ua.ricardomartins.qualar.TutorialSlide;

/**
 * Created by ricardo on 15/06/16.
 */
public class Tutorial extends AppIntro {
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(hasSeenTutorial()){
            Intent intent = new Intent(Tutorial.this, Login.class);
            startActivity(intent);
            finish();
        }
        else{
            loadTutorial();
        }
    }

    private boolean hasSeenTutorial() {
        Boolean hasSeenTutorial = mSharedPreferences.getBoolean(Common.USER_SAW_TUTORIAL, false);
        return hasSeenTutorial;
    }

    public void loadTutorial(){
        addSlide(AppIntroFragment.newInstance("Seja Bem Vindo ao LabQAr", "O LabQAr permite a divulgação em tempo real do estado da qualidade do ar no local.", R.drawable.logo, getResources().getColor(R.color.colorPrimaryDark)));
        addSlide(AppIntroFragment.newInstance("Classificação do estado do ar", "O LabQAr permite saber a classificação global da qualidade do ar", R.drawable.overview, getResources().getColor(R.color.colorPrimaryDark)));
        addSlide(AppIntroFragment.newInstance("Classificação de cada poluente", "O LabQAr permite saber a classificação dos diferentes poluentes que entram no cálculo da qualidade global do ar", R.drawable.distribution, getResources().getColor(R.color.colorPrimaryDark)));
        addSlide(AppIntroFragment.newInstance("Variação de cada parâmetro nas últimas 24 Horas", "O LabQAr permite observar a variação dos diferentes parametros nas ultimas 24 horas registadas...", R.drawable.variation, getResources().getColor(R.color.colorPrimaryDark)));
        addSlide(AppIntroFragment.newInstance("Variação de cada parâmetro nas últimas 24 Horas", "Com possibilidade de zoom em ambas as direções", R.drawable.variation_zoom, getResources().getColor(R.color.colorPrimaryDark)));

        // OPTIONAL METHODS
        // Override bar/separatorolor.
        showStatusBar(true);
        setBarColor(getResources().getColor(R.color.black));
        setSeparatorColor(getResources().getColor(R.color.colorAccent));

        setFadeAnimation();

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        mSharedPreferences.edit().putBoolean(Common.USER_SAW_TUTORIAL, true).apply();
        Intent intent = new Intent(Tutorial.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        mSharedPreferences.edit().putBoolean(Common.USER_SAW_TUTORIAL, true).apply();
        Intent intent = new Intent(Tutorial.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}

