package com.github.salimkayabasi.dialogspinner.example;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.salimkayabasi.dialogspinner.DialogSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivityFragment extends Fragment {

  @Bind(R.id.spinner_cities)
  protected DialogSpinner<String> citiesSpinner;
  @Bind(R.id.spinner_numbers)
  protected DialogSpinner<Integer> numberSpinner;
  @Bind(R.id.spinner_foods)
  protected DialogSpinner<String> foodSpinner;
  @Bind(R.id.spinner_multi)
  protected DialogSpinner<String> multiSpinner;

  public MainActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    List<String> cities = new ArrayList<>();
    cities.add("Istanbul");
    cities.add("San Fransisco");
    cities.add("New York");
    citiesSpinner.setItems(cities);

    List<Integer> numbers = new ArrayList<>();
    numbers.add(1);
    numbers.add(2);
    numbers.add(3);
    numberSpinner.setItems(numbers);

    List<String> foods = new ArrayList<>();
    foods.add("Milk");
    foods.add("Egg");
    foods.add("Olive oil");
    foodSpinner.setItems(foods);

    multiSpinner.setItems(foods);
  }
}