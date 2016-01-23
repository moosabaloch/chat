package pana.com.chat.ui.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import pana.com.chat.Adaptor.PagerAdaptor;
import pana.com.chat.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<Fragment> fragments;
    private FloatingActionButton fab;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public TabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFragment newInstance(String param1, String param2) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TABFRAG", "onCreate");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TABFRAG", "onCreateView");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        fragments = new ArrayList<>(4);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        View view1 = inflater.inflate(R.layout.tab_first, null);
        View view2 = inflater.inflate(R.layout.tab_second, null);
        View view3 = inflater.inflate(R.layout.tab_third, null);
        View view4 = inflater.inflate(R.layout.tab_forth, null);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view1));
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));
        tabLayout.addTab(tabLayout.newTab().setCustomView(view4));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        fragments.add(0, new HomeFragment());
        fragments.add(1, new GroupFragment());
        fragments.add(2, new FriendsFragment());
        fragments.add(3, new FriendsRequestFragment());
        viewPager.setAdapter(new PagerAdaptor(getActivity().getSupportFragmentManager(), fragments));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                //  fab.setImageResource(picture.get(tab.getPosition()));
                //fab.setBackgroundResource(picture.get(tab.getPosition()));
                //   message = tab.getPosition();

                switch (tab.getPosition()) {
                    case 0:
                        fab.setVisibility(View.GONE);
                        break;
                    case 1:
                        fabActions(View.VISIBLE,R.drawable.add,new SearchGroupFragment());

                        break;
                    case 2:
                        fabActions(View.VISIBLE,R.drawable.addfriend,new AddFriendFragment());
                        break;
                    case 3:
                        fab.setVisibility(View.GONE);
                        TabInterAction tabInterAction = (TabInterAction) fragments.get(3);
                        tabInterAction.friendRequestFragment();
                        break;
                    default:
                        fab.setVisibility(View.GONE);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Toast.makeText(getActivity(), "Select Ho Gaya na Bus Ker Ab", Toast.LENGTH_LONG).show();
            }
        });

        return view;

    }

    private void fabActions(int visibility, int imageResource, final Fragment fragment) {
        fab.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            fab.setImageResource(imageResource);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction().addToBackStack("").add(R.id.homeActivityContent, fragment).commit();

                    fab.setVisibility(View.GONE);
                }
            });
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("TABFRAG", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TABFRAG", "onStop");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("TABFRAG", "onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TABFRAG", "onResume");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TABFRAG", "onDestroy");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("TABFRAG", "onDestroyView");

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface TabInterAction {
        void friendRequestFragment();
    }
}
