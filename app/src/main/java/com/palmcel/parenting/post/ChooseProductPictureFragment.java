package com.palmcel.parenting.post;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.common.collect.Lists;
import com.palmcel.parenting.R;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.model.ProductPageInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChooseProductPictureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChooseProductPictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ChooseProductPictureFragment extends Fragment {

    private static final String TAG = "ChooseProductPictureFragment";
    private static final String ARG_PRODUCT_PAGE_INFO = "productPageInfo";

    private Context mContext;
    private ProductPageInfo mProductPageInfo;
    private ArrayList<ImageView> mPictureImageViews;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param productPageInfo The product page info which includes a list of image urls
     *                        for the product
     * @return A new instance of fragment ChooseProductPictureFragment.
     */
    public static ChooseProductPictureFragment newInstance(ProductPageInfo productPageInfo) {
        ChooseProductPictureFragment fragment = new ChooseProductPictureFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_PAGE_INFO, productPageInfo);
        fragment.setArguments(args);
        return fragment;
    }

    public ChooseProductPictureFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProductPageInfo =
                    (ProductPageInfo) getArguments().getSerializable(ARG_PRODUCT_PAGE_INFO);
            Log.d(TAG, "onCreate, mProductPageInfo=" + mProductPageInfo);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.choose_product_picture_fragment, container, false);

        mPictureImageViews = Lists.newArrayList();
        mPictureImageViews.add((ImageView) rootView.findViewById(R.id.picture_1));
        mPictureImageViews.add((ImageView) rootView.findViewById(R.id.picture_2));
        mPictureImageViews.add((ImageView) rootView.findViewById(R.id.picture_3));
        mPictureImageViews.add((ImageView) rootView.findViewById(R.id.picture_4));
        mPictureImageViews.add((ImageView) rootView.findViewById(R.id.picture_5));
        mPictureImageViews.add((ImageView) rootView.findViewById(R.id.picture_6));

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int imageCount = mProductPageInfo.productPictureUrls.size();
        for (int i = 0; i < imageCount; i++) {
            final String imageUrl = mProductPageInfo.productPictureUrls.get(i);
            ImageView pictureImageView = mPictureImageViews.get(i);
            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_wait)
                    .into(pictureImageView);

            pictureImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onChooseProductPicture(imageUrl, mProductPageInfo);
                    }
                }
            });
        }

        int imageViewCount = mPictureImageViews.size();
        for (int i = imageCount; i < imageViewCount; i++) {
            mPictureImageViews.get(i).setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        try {
           mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onChooseProductPicture(
                String chosenProductPictureUrl, ProductPageInfo productPageInfo);
    }
}
