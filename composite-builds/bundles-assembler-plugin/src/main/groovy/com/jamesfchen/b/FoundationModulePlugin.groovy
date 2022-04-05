package com.jamesfchen.b

class FoundationModulePlugin extends AndroidWithMavenPlugin{
    @Override
    String mainPlugin() {
        return 'com.android.library'
    }
}