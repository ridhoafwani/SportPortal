// Generated by view binder compiler. Do not edit!
package com.sportportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.sportportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityAdminDetailLapanganBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final AppBarLayout appBarLayout;

  @NonNull
  public final ImageButton btToggleCourt;

  @NonNull
  public final ImageButton btToggleLokasi;

  @NonNull
  public final ImageButton btToggleUnavailable;

  @NonNull
  public final MaterialButton btnDelete;

  @NonNull
  public final AppCompatButton btnMap;

  @NonNull
  public final TextView category;

  @NonNull
  public final CollapsingToolbarLayout collapsingToolbar;

  @NonNull
  public final RecyclerView courtList;

  @NonNull
  public final ImageView image;

  @NonNull
  public final LinearLayout lytExpandCourt;

  @NonNull
  public final LinearLayout lytExpandLokasi;

  @NonNull
  public final LinearLayout lytExpandUnavailable;

  @NonNull
  public final NestedScrollView nestedScrollView;

  @NonNull
  public final CoordinatorLayout parentView;

  @NonNull
  public final TextView title;

  @NonNull
  public final Toolbar toolbar;

  @NonNull
  public final TextView tvLokasi;

  @NonNull
  public final RecyclerView unavailableList;

  private ActivityAdminDetailLapanganBinding(@NonNull CoordinatorLayout rootView,
      @NonNull AppBarLayout appBarLayout, @NonNull ImageButton btToggleCourt,
      @NonNull ImageButton btToggleLokasi, @NonNull ImageButton btToggleUnavailable,
      @NonNull MaterialButton btnDelete, @NonNull AppCompatButton btnMap,
      @NonNull TextView category, @NonNull CollapsingToolbarLayout collapsingToolbar,
      @NonNull RecyclerView courtList, @NonNull ImageView image,
      @NonNull LinearLayout lytExpandCourt, @NonNull LinearLayout lytExpandLokasi,
      @NonNull LinearLayout lytExpandUnavailable, @NonNull NestedScrollView nestedScrollView,
      @NonNull CoordinatorLayout parentView, @NonNull TextView title, @NonNull Toolbar toolbar,
      @NonNull TextView tvLokasi, @NonNull RecyclerView unavailableList) {
    this.rootView = rootView;
    this.appBarLayout = appBarLayout;
    this.btToggleCourt = btToggleCourt;
    this.btToggleLokasi = btToggleLokasi;
    this.btToggleUnavailable = btToggleUnavailable;
    this.btnDelete = btnDelete;
    this.btnMap = btnMap;
    this.category = category;
    this.collapsingToolbar = collapsingToolbar;
    this.courtList = courtList;
    this.image = image;
    this.lytExpandCourt = lytExpandCourt;
    this.lytExpandLokasi = lytExpandLokasi;
    this.lytExpandUnavailable = lytExpandUnavailable;
    this.nestedScrollView = nestedScrollView;
    this.parentView = parentView;
    this.title = title;
    this.toolbar = toolbar;
    this.tvLokasi = tvLokasi;
    this.unavailableList = unavailableList;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityAdminDetailLapanganBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityAdminDetailLapanganBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_admin_detail_lapangan, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityAdminDetailLapanganBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.app_bar_layout;
      AppBarLayout appBarLayout = ViewBindings.findChildViewById(rootView, id);
      if (appBarLayout == null) {
        break missingId;
      }

      id = R.id.bt_toggle_court;
      ImageButton btToggleCourt = ViewBindings.findChildViewById(rootView, id);
      if (btToggleCourt == null) {
        break missingId;
      }

      id = R.id.bt_toggle_lokasi;
      ImageButton btToggleLokasi = ViewBindings.findChildViewById(rootView, id);
      if (btToggleLokasi == null) {
        break missingId;
      }

      id = R.id.bt_toggle_unavailable;
      ImageButton btToggleUnavailable = ViewBindings.findChildViewById(rootView, id);
      if (btToggleUnavailable == null) {
        break missingId;
      }

      id = R.id.btn_delete;
      MaterialButton btnDelete = ViewBindings.findChildViewById(rootView, id);
      if (btnDelete == null) {
        break missingId;
      }

      id = R.id.btn_map;
      AppCompatButton btnMap = ViewBindings.findChildViewById(rootView, id);
      if (btnMap == null) {
        break missingId;
      }

      id = R.id.category;
      TextView category = ViewBindings.findChildViewById(rootView, id);
      if (category == null) {
        break missingId;
      }

      id = R.id.collapsing_toolbar;
      CollapsingToolbarLayout collapsingToolbar = ViewBindings.findChildViewById(rootView, id);
      if (collapsingToolbar == null) {
        break missingId;
      }

      id = R.id.courtList;
      RecyclerView courtList = ViewBindings.findChildViewById(rootView, id);
      if (courtList == null) {
        break missingId;
      }

      id = R.id.image;
      ImageView image = ViewBindings.findChildViewById(rootView, id);
      if (image == null) {
        break missingId;
      }

      id = R.id.lyt_expand_court;
      LinearLayout lytExpandCourt = ViewBindings.findChildViewById(rootView, id);
      if (lytExpandCourt == null) {
        break missingId;
      }

      id = R.id.lyt_expand_lokasi;
      LinearLayout lytExpandLokasi = ViewBindings.findChildViewById(rootView, id);
      if (lytExpandLokasi == null) {
        break missingId;
      }

      id = R.id.lyt_expand_unavailable;
      LinearLayout lytExpandUnavailable = ViewBindings.findChildViewById(rootView, id);
      if (lytExpandUnavailable == null) {
        break missingId;
      }

      id = R.id.nested_scroll_view;
      NestedScrollView nestedScrollView = ViewBindings.findChildViewById(rootView, id);
      if (nestedScrollView == null) {
        break missingId;
      }

      CoordinatorLayout parentView = (CoordinatorLayout) rootView;

      id = R.id.title;
      TextView title = ViewBindings.findChildViewById(rootView, id);
      if (title == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      id = R.id.tv_lokasi;
      TextView tvLokasi = ViewBindings.findChildViewById(rootView, id);
      if (tvLokasi == null) {
        break missingId;
      }

      id = R.id.unavailableList;
      RecyclerView unavailableList = ViewBindings.findChildViewById(rootView, id);
      if (unavailableList == null) {
        break missingId;
      }

      return new ActivityAdminDetailLapanganBinding((CoordinatorLayout) rootView, appBarLayout,
          btToggleCourt, btToggleLokasi, btToggleUnavailable, btnDelete, btnMap, category,
          collapsingToolbar, courtList, image, lytExpandCourt, lytExpandLokasi,
          lytExpandUnavailable, nestedScrollView, parentView, title, toolbar, tvLokasi,
          unavailableList);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}