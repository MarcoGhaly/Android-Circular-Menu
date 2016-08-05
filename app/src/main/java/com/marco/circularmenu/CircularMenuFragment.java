package com.marco.circularmenu;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CircularMenuFragment extends Fragment implements View.OnClickListener {

    // Circular Menu Fragment Callback
    public interface CircularMenuFragmentCallback {
        void centerItemClicked();

        void itemClicked(int index);
    }


    private static final String ARGUMENT_MENU_ITEM = "Menu Items";
    private static final String ARGUMENT_CENTER_ICON_RESOURCE = "Center Icon Resource";
    private static final String ARGUMENT_ITEMS_BACKGROUND_RESOURCE = "Items Background Resource";
    private static final String ARGUMENT_TOTAL_ANGLE = "Total Angle";
    private static final String ARGUMENT_START_ANGLE = "Start Angle";
    private static final String ARGUMENT_CENTER_ICON_DIMENSION_RATIO = "Center Icon Dimension Ratio";
    private static final String ARGUMENT_ICONS_DIMENSION_RATIO = "Image Dimension Ratio";
    private static final String ARGUMENT_FONT_SIZE_SP = "Font Size";
    private static final String ARGUMENT_TEXT_COLOR = "Text Color";


    // New Instance
    public static CircularMenuFragment newInstance(CircularMenuItem[] circularMenuItems, int centerIconResourceID,
                                                   int itemsBackgroundResource) {
        return newInstance(circularMenuItems, centerIconResourceID, itemsBackgroundResource,
                0, Math.PI * 2, 0.45, 0.175, 13, Color.BLACK);
    }

    public static CircularMenuFragment newInstance(CircularMenuItem[] circularMenuItems, int centerIconResourceID,
                                                   int itemsBackgroundResource, double startAngle, double totalAngle,
                                                   double centerIconDimensionRatio, double imageDimensionRatio,
                                                   int fontSizeSP, int textColor) {
        CircularMenuFragment circularMenuFragment = new CircularMenuFragment();

        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGUMENT_MENU_ITEM, circularMenuItems);
        arguments.putInt(ARGUMENT_CENTER_ICON_RESOURCE, centerIconResourceID);
        arguments.putInt(ARGUMENT_ITEMS_BACKGROUND_RESOURCE, itemsBackgroundResource);
        arguments.putDouble(ARGUMENT_START_ANGLE, startAngle);
        arguments.putDouble(ARGUMENT_TOTAL_ANGLE, totalAngle);
        arguments.putDouble(ARGUMENT_CENTER_ICON_DIMENSION_RATIO, centerIconDimensionRatio);
        arguments.putDouble(ARGUMENT_ICONS_DIMENSION_RATIO, imageDimensionRatio);
        arguments.putInt(ARGUMENT_FONT_SIZE_SP, fontSizeSP);
        arguments.putInt(ARGUMENT_TEXT_COLOR, textColor);
        circularMenuFragment.setArguments(arguments);

        return circularMenuFragment;
    }


    private CircularMenuFragmentCallback circularMenuFragmentCallback;

    private ViewGroup layout_container;

    private CircularMenuItem[] circularMenuItems;
    private int centerIconResourceID;
    private ImageButton centerButton;
    private int itemsBackgroundResource;

    private double startAngle;
    private double totalAngle;

    private double centerIconDimensionRatio;
    private double iconsDimensionRatio;
    private int fontSizeSP;
    private int textColor;

    private int containerWidth;
    private int containerHeight;

    private int smallerDimension;
    private int imageDimension;
    private int itemDimension;

    private double radius;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Bundle arguments = getArguments();
        circularMenuItems = (CircularMenuItem[]) arguments.getSerializable(ARGUMENT_MENU_ITEM);
        centerIconResourceID = arguments.getInt(ARGUMENT_CENTER_ICON_RESOURCE);
        itemsBackgroundResource = arguments.getInt(ARGUMENT_ITEMS_BACKGROUND_RESOURCE);
        totalAngle = arguments.getDouble(ARGUMENT_TOTAL_ANGLE);
        startAngle = arguments.getDouble(ARGUMENT_START_ANGLE);
        centerIconDimensionRatio = arguments.getDouble(ARGUMENT_CENTER_ICON_DIMENSION_RATIO);
        iconsDimensionRatio = arguments.getDouble(ARGUMENT_ICONS_DIMENSION_RATIO);
        fontSizeSP = arguments.getInt(ARGUMENT_FONT_SIZE_SP);
        textColor = arguments.getInt(ARGUMENT_TEXT_COLOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_circular_menu, container, false);

        layout_container = (ViewGroup) rootView.findViewById(R.id.layout_container);

        layout_container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                containerWidth = layout_container.getMeasuredWidth();
                containerHeight = layout_container.getMeasuredHeight();

                smallerDimension = containerWidth < containerHeight ? containerWidth : containerHeight;
                imageDimension = (int) (smallerDimension * iconsDimensionRatio);
                itemDimension = 0;

                createItems();
                createCenterButton();

                ViewTreeObserver viewTreeObserver = layout_container.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= 16) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this);
                }
            }
        });

        return rootView;
    }


    // Create Items
    private void createItems() {
        int anglesNumber = circularMenuItems.length;
        if (totalAngle != Math.PI * 2) {
            anglesNumber -= 1;
        }
        double theta = totalAngle / anglesNumber;

        double angle = startAngle;
        for (int i = 0; i < circularMenuItems.length; i++) {
            View view = createItem(circularMenuItems[i]);

            double slope = Math.tan(angle);
            double centerX = Math.sqrt(Math.pow(radius, 2) / (Math.pow(slope, 2) + 1)) * Math.signum(Math.cos(angle));
            double centerY = Math.sqrt(Math.pow(radius, 2) - Math.pow(centerX, 2)) * Math.signum(Math.sin(angle));

            centerX = centerX + containerWidth / 2;
            centerY = centerY * -1 + containerHeight / 2;

            int startX = (int) (centerX - itemDimension / 2);
            int startY = (int) (centerY - itemDimension / 2);

            RelativeLayout.LayoutParams itemLayoutParams = new RelativeLayout.LayoutParams(
                    itemDimension, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemLayoutParams.leftMargin = startX;
            itemLayoutParams.topMargin = startY;

            view.setTag(i);
            view.setOnClickListener(this);

            view.setLayoutParams(itemLayoutParams);
            layout_container.addView(view);

            angle += theta;
        }
    }


    // Create Item
    private ViewGroup createItem(CircularMenuItem circularMenuItem) {
        TextView textView = new TextView(getActivity());
        textView.setText(circularMenuItem.getText());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeSP);
        textView.setTextColor(textColor);
        textView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayoutParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(textLayoutParams);

        int imagePadding = imageDimension / 5;
        int separatorMargin = imageDimension / 25;

        if (itemDimension == 0) {
            itemDimension = imageDimension + textView.getLineHeight() + separatorMargin * 2;
        }

        radius = (double) smallerDimension / 2 - itemDimension / 2;

        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(circularMenuItem.getIconID());

        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(imageDimension, imageDimension);
        imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        imageView.setLayoutParams(imageLayoutParams);
        imageView.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
        imageView.setBackgroundResource(itemsBackgroundResource);

        View separator = new View(getActivity());

        LinearLayout.LayoutParams separatorLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        separatorLayoutParams.setMargins(separatorMargin, separatorMargin, separatorMargin, separatorMargin);
        separator.setLayoutParams(separatorLayoutParams);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(imageView);
        layout.addView(separator);
        layout.addView(textView);

        return layout;
    }


    // Create Center Button
    private void createCenterButton() {
        int centerButtonDimension = (int) (smallerDimension * centerIconDimensionRatio);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(centerButtonDimension, centerButtonDimension);
        layoutParams.leftMargin = (containerWidth - centerButtonDimension) / 2;
        layoutParams.topMargin = (containerHeight - centerButtonDimension) / 2;

        centerButton = new ImageButton(getActivity());
        centerButton.setLayoutParams(layoutParams);
        centerButton.setImageResource(centerIconResourceID);
        centerButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        centerButton.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 16) {
            centerButton.setBackground(null);
        } else {
            centerButton.setBackgroundDrawable(null);
        }

        layout_container.addView(centerButton);
    }


    // Set On Circular Menu Item Click Listener
    public void setCircularMenuFragmentCallback(CircularMenuFragmentCallback circularMenuFragmentCallback) {
        this.circularMenuFragmentCallback = circularMenuFragmentCallback;
    }


    @Override
    public void onClick(View view) {
        if (view == centerButton) {
            circularMenuFragmentCallback.centerItemClicked();
        } else {
            int tag = (int) view.getTag();
            circularMenuFragmentCallback.itemClicked(tag);
        }
    }

}
