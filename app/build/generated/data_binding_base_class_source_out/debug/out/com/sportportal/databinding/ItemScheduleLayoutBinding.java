// Generated by view binder compiler. Do not edit!
package com.sportportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.sportportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemScheduleLayoutBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final View foreground;

  @NonNull
  public final TextView jam;

  @NonNull
  public final LinearLayout layout;

  @NonNull
  public final TextView price;

  @NonNull
  public final CardView priceCardView;

  private ItemScheduleLayoutBinding(@NonNull CardView rootView, @NonNull View foreground,
      @NonNull TextView jam, @NonNull LinearLayout layout, @NonNull TextView price,
      @NonNull CardView priceCardView) {
    this.rootView = rootView;
    this.foreground = foreground;
    this.jam = jam;
    this.layout = layout;
    this.price = price;
    this.priceCardView = priceCardView;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemScheduleLayoutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemScheduleLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_schedule_layout, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemScheduleLayoutBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.foreground;
      View foreground = ViewBindings.findChildViewById(rootView, id);
      if (foreground == null) {
        break missingId;
      }

      id = R.id.jam;
      TextView jam = ViewBindings.findChildViewById(rootView, id);
      if (jam == null) {
        break missingId;
      }

      id = R.id.layout;
      LinearLayout layout = ViewBindings.findChildViewById(rootView, id);
      if (layout == null) {
        break missingId;
      }

      id = R.id.price;
      TextView price = ViewBindings.findChildViewById(rootView, id);
      if (price == null) {
        break missingId;
      }

      CardView priceCardView = (CardView) rootView;

      return new ItemScheduleLayoutBinding((CardView) rootView, foreground, jam, layout, price,
          priceCardView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}