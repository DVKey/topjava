package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMeal> list = new ArrayList<>();
        Map<LocalDate, Integer> map = new HashMap<>();
        for (UserMeal meal : meals) {
            LocalDateTime dateTime = meal.getDateTime();
            LocalDate date = dateTime.toLocalDate();
            int calories = meal.getCalories();

            map.put(date, calories + map.getOrDefault(date, 0));

            if (TimeUtil.isBetweenHalfOpen(dateTime.toLocalTime(), startTime, endTime))
                list.add(meal);
        }

        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : list)
            result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories()
                    , map.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));

        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMeal> list = new ArrayList<>();
        Map<LocalDate, Integer> map = new HashMap<>();
        meals.forEach(meal -> {
            LocalDateTime dateTime = meal.getDateTime();
            LocalDate date = dateTime.toLocalDate();
            map.put(date, meal.getCalories() + map.getOrDefault(date, 0));
            if (TimeUtil.isBetweenHalfOpen(dateTime.toLocalTime(), startTime, endTime))
                list.add(meal);
        });

        return list.stream().map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories()
                , map.get(meal.getDateTime().toLocalDate()) > caloriesPerDay)).collect(Collectors.toList());

/* правильное решение:

        Map<LocalDate, Integer> caloriesSumByDate = meals.stream().collect(Collectors.groupingBy(um -> um.getDateTime().toLocalDate(), Collectors.summingInt(UserMeals::getCalories())));

        return meals.stream()
                .filter(um -> TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime))
                .map(um -> new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories()
                        , caloriesSumByDate.get(um.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
*/
    }
}
