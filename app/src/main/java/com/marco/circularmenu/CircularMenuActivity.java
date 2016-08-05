package com.marco.circularmenu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class CircularMenuActivity extends AppCompatActivity implements CircularMenuFragment.CircularMenuFragmentCallback {

    private CircularMenuItem[] circularMenuItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular_menu);

        initCircularMenu();
    }


    // Initialize Circular Menu
    private void initCircularMenu() {
        circularMenuItems = new CircularMenuItem[6];
        circularMenuItems[0] = new CircularMenuItem(R.drawable.apple, getString(R.string.apple));
        circularMenuItems[1] = new CircularMenuItem(R.drawable.orange, getString(R.string.orange));
        circularMenuItems[2] = new CircularMenuItem(R.drawable.banana, getString(R.string.banana));
        circularMenuItems[3] = new CircularMenuItem(R.drawable.pineapple, getString(R.string.pineapple));
        circularMenuItems[4] = new CircularMenuItem(R.drawable.strawberry, getString(R.string.strawberry));
        circularMenuItems[5] = new CircularMenuItem(R.drawable.pear, getString(R.string.pear));

        FragmentManager fragmentManager = getFragmentManager();
        CircularMenuFragment circularMenuFragment = (CircularMenuFragment) fragmentManager.findFragmentByTag("Items Fragment");
        if (circularMenuFragment == null) {
            circularMenuFragment = CircularMenuFragment.newInstance(circularMenuItems, R.drawable.fruit,
                    R.drawable.background_menu_item, 0, Math.PI * 2, 0.45, 0.175, 13, Color.BLACK);
            circularMenuFragment.setCircularMenuFragmentCallback(this);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.layout_circularMenu, circularMenuFragment, "Items Fragment");
            fragmentTransaction.commit();
        }
    }


    @Override
    public void centerItemClicked() {
        String text = getString(R.string.fruit) + " Clicked";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void itemClicked(int index) {
        CircularMenuItem circularMenuItem = circularMenuItems[index];
        String text = circularMenuItem.getText() + " Clicked";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
