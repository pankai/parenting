package com.palmcel.parenting.post;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.common.base.Strings;
import com.google.common.collect.ObjectArrays;
import com.palmcel.parenting.R;
import com.palmcel.parenting.common.Constants;
import com.palmcel.parenting.model.PostPublicity;
import com.palmcel.parenting.model.PostSetting;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComposeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ComposeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IS_PRODUCT = "isProduct";
    private static final String ARG_PRODUCT_PIC_URL = "productPictureUrl";

    private Context mContext;
    private View mPostButton;
    private EditText mPostEdit;
    private Spinner mPublicitySpinner;
    private Spinner mIsAnonymousSpinner;
    private Spinner mUsefulForGenderSpinner;
    private Spinner mUsefulForAgeFromSpinner;
    private Spinner mUsefulForAgeToSpinner;
    private View mPicturePreviewLayout;
    private ImageView mPicturePreviewView;
    private View mPicturePreviewRemoveButton;

    // Is it composer for product
    private boolean mIsProduct;
    // Product picture url if mIsProduct is true.
    private String mProductPictureUrl;

    private OnFragmentInteractionListener mListener;

    /**
     * @param isProduct Is composing for product
     * @param productPictureUrl url for product image
     * @return A new instance of fragment ComposeFragment.
     */
    public static ComposeFragment newInstance(
            boolean isProduct,
            @Nullable String productPictureUrl) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_PRODUCT, isProduct);
        args.putString(ARG_PRODUCT_PIC_URL, productPictureUrl);
        fragment.setArguments(args);
        return fragment;
    }
    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsProduct = getArguments().getBoolean(ARG_IS_PRODUCT);
            mProductPictureUrl = getArguments().getString(ARG_PRODUCT_PIC_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.compose_fragment, container, false);

        mPostButton = rootView.findViewById(R.id.post_button);
        mPostEdit = (EditText) rootView.findViewById(R.id.compose_edit);
        mPublicitySpinner = (Spinner) rootView.findViewById(R.id.publicity_sprinner);
        mIsAnonymousSpinner = (Spinner) rootView.findViewById(R.id.is_anonymous_spinner);
        mUsefulForGenderSpinner = (Spinner) rootView.findViewById(R.id.useful_for_gender_spinner);
        mUsefulForAgeFromSpinner = (Spinner) rootView.findViewById(R.id.useful_from_spinner);
        mUsefulForAgeToSpinner = (Spinner) rootView.findViewById(R.id.useful_to_spinner);
        mPicturePreviewLayout = rootView.findViewById(R.id.picture_preview_layout);
        mPicturePreviewView = (ImageView) rootView.findViewById(R.id.picture_review);
        mPicturePreviewRemoveButton = rootView.findViewById(R.id.remove_picture_button);

        setupPostSettings(rootView);

        return rootView;
    }

    /**
     * Set up post settings (spinners) for this post.
     * @param rootView
     */
    private void setupPostSettings(ViewGroup rootView) {
        String[] categoriesWithNA = ObjectArrays.concat(getResources().getString(R.string.na),
                Constants.getPostCategories());

        mUsefulForAgeFromSpinner = (Spinner) rootView.findViewById(R.id.useful_from_spinner);
        ArrayAdapter adapter = new ArrayAdapter(
                mContext,
                android.R.layout.simple_spinner_item,
                categoriesWithNA);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUsefulForAgeFromSpinner.setAdapter(adapter);
        mUsefulForAgeFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mUsefulForAgeToSpinner.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mUsefulForAgeToSpinner = (Spinner) rootView.findViewById(R.id.useful_to_spinner);
        adapter = new ArrayAdapter(
                mContext,
                android.R.layout.simple_spinner_item,
                categoriesWithNA);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUsefulForAgeToSpinner.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postMessage = mPostEdit.getText().toString();
                if (mListener != null && !Strings.isNullOrEmpty(postMessage)) {
                    mListener.onSubmitPost(postMessage, getPostSettings());
                }
            }
        });

        if (mIsProduct && !Strings.isNullOrEmpty(mProductPictureUrl)) {
            // Show product preview view
            mPicturePreviewLayout.setVisibility(View.VISIBLE);
            mPicturePreviewRemoveButton.setVisibility(View.GONE);
            Picasso.with(mContext)
                    .load(mProductPictureUrl)
                    .placeholder(R.drawable.ic_wait)
                    .into(mPicturePreviewView);
        }
    }

    private PostSetting getPostSettings() {
        PostPublicity postPublicity = PostPublicity.Public;
        String selectedPublicity = mPublicitySpinner.getSelectedItem().toString();
        if (getResources().getString(R.string.publicity_followers_only).equals(selectedPublicity)) {
            postPublicity = PostPublicity.FollowersOnly;
        } else  if (
                getResources().getString(R.string.publicity_private).equals(selectedPublicity)) {
            postPublicity = PostPublicity.Private;
        }

        String selectedIsAnonymous = mIsAnonymousSpinner.getSelectedItem().toString();
        boolean isAnonymous = getResources().getString(R.string.yes).equals(selectedIsAnonymous);

        String forGender = null;
        String selectedForGender = mUsefulForGenderSpinner.getSelectedItem().toString();
        if (getResources().getString(R.string.useful_for_girls).equals(selectedForGender)) {
            forGender = Constants.FOR_GENDER_FEMALE;
        } else if (getResources().getString(R.string.useful_for_boys).equals(selectedForGender)) {
            forGender = Constants.FOR_GENDER_MALE;
        }

        String forAgeFrom = null;
        String selectedForAgeFrom = mUsefulForAgeFromSpinner.getSelectedItem().toString();
        if (!getResources().getString(R.string.na).equals(selectedForAgeFrom)) {
            // TODO: i18n consideration for assigning selectedForAgeFrom to forAgeFrom.
            forAgeFrom = selectedForAgeFrom;
        }

        String forAgeTo = null;
        String selectedForAgeTo = mUsefulForAgeToSpinner.getSelectedItem().toString();
        if (!getResources().getString(R.string.na).equals(selectedForAgeTo)) {
            // TODO: i18n consideration for assigning selectedForAgeFrom to forAgeFrom.
            forAgeTo = selectedForAgeTo;
        }

        return new PostSetting(postPublicity, isAnonymous, forGender, forAgeFrom, forAgeTo);
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
        public void onSubmitPost(String message, PostSetting postSetting);
    }
}
