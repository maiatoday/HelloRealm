package net.maiatday.hellorealm;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Application Class
 * Created by maia on 2017/01/22.
 */

public class HelloRealmApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
//                .name("moody.realm")
//                .build();
//        Realm.setDefaultConfiguration(realmConfiguration);
        Realm.init(this);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }
}
