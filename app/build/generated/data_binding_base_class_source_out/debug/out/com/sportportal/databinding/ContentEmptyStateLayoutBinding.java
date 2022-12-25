// Generated by view binder compiler. Do not edit!
package com.sportportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.airbnb.lottie.LottieAnimationView;
import com.sportportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ContentEmptyStateLayoutBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView emptyStateDescription;

  @NonNull
  public final LottieAnimationView emptyStateImage;

  @NonNull
  public final TextView emptyStateTitle;

  @NonNull
  public final RelativeLayout emptyStateView;

  private ContentEmptyStateLayoutBinding(@NonNull RelativeLayout rootView,
      @NonNull TextView emptyStateDescription, @NonNull LottieAnimationView emptyStateImage,
      @NonNull TextView emptyStateTitle, @NonNull RelativeLayout emptyStateView) {
    this.rootView = rootView;
    this.emptyStateDescription = emptyStateDescription;
    this.emptyStateImage = emptyStateImage;
    this.emptyStateTitle = emptyStateTitle;
    this.emptyStateView = emptyStateView;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ContentEmptyStateLayoutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ContentEmptyStateLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.content_empty_state_layout, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ContentEmptyStateLayoutBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.empty_state_description;
      TextView emptyStateDescription = ViewBindings.findChildViewById(rootView, id);
      if (emptyStateDescription == null) {
        break missingId;
      }

      id = R.id.empty_state_image;
      LottieAnimationView emptyStateImage = ViewBindings.findChildViewById(rootView, id);
      if (emptyStateImage == null) {
        break missingId;
      }

      id = R.id.empty_state_title;
      TextView emptyStateTitle = ViewBindings.findChildViewById(rootView, id);
      if (emptyStateTitle == null) {
        break missingId;
      }

      RelativeLayout emptyStateView = (RelativeLayout) rootView;

      return new ContentEmptyStateLayoutBinding((RelativeLayout) rootView, emptyStateDescription,
          emptyStateImage, emptyStateTitle, emptyStateView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}