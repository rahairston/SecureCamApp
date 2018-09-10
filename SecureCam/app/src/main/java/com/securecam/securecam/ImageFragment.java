package com.securecam.securecam;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String IMAGE_ARG = "image";
    private static final String NAME_ARG = "name";

    // TODO: Rename and change types of parameters
    private byte[] byteArr;
    private Bitmap bmp;
    private String name;
    private PhotoView image;
    private ImageView closeButton;
    private ImageView saveButton;
    private TextView nameText;
    private ImageFragment fragment;

    private OnFragmentInteractionListener mListener;

    public ImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param b Bitmap byte array.
     * @return A new instance of fragment ImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageFragment newInstance(byte[] b, String name) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putByteArray(IMAGE_ARG, b);
        args.putString(NAME_ARG, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(NAME_ARG);
            byteArr = getArguments().getByteArray(IMAGE_ARG);
            bmp = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        fragment = this;

        nameText = (TextView)view.findViewById(R.id.name);
        nameText.setText(name);

        closeButton = (ImageView)view.findViewById(R.id.closeButton);
        closeButton.setElevation(1);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                // Check to see if the fragment is already showing.

                if (fragment != null) {
                    ((MainActivity) getActivity()).returnFromFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragment).commit();
                }
            }
        });


        saveButton = (ImageView)view.findViewById(R.id.saveButton);
        saveButton.setElevation(1);

        image = (PhotoView) view.findViewById(R.id.image);
        image.setImageBitmap(bmp);
        image.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setAdjustViewBounds(true);
        image.setElevation(0);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri.toString());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
        public void onFragmentInteraction(String uri);
    }
}
