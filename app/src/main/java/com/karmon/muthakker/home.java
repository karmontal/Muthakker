package com.karmon.muthakker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by karmo on 24/03/2016.
 */
public class home extends AppCompatActivity{
    private ShareActionProvider mShareActionProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);

        MenuItem shareItem = menu.findItem(R.id.shareButton);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setShareIntent(setShareIntent());
        return super.onCreateOptionsMenu(menu);
    }

    private Intent setShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Subject");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "My Extra Text");
        mShareActionProvider.setShareIntent(shareIntent);
        return shareIntent;
    }
}
