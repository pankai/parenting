package com.palmcel.parenting.post;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.palmcel.parenting.R;
import com.palmcel.parenting.common.Log;

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
    private static final String ARG_IMAGE_URLS = "imageUrls";

    private ArrayList<String> mImageUrls;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageUrls List of image urls for the product
     * @return A new instance of fragment ChooseProductPictureFragment.
     */
    public static ChooseProductPictureFragment newInstance(ArrayList<String> imageUrls) {
        ChooseProductPictureFragment fragment = new ChooseProductPictureFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_IMAGE_URLS, imageUrls);
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
            mImageUrls = getArguments().getStringArrayList(ARG_IMAGE_URLS);
            Log.d(TAG, "onCreate, mImageUrls=" + mImageUrls);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.choose_product_picture_fragment, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
           // mListener = (OnFragmentInteractionListener) activity;
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
