package com.shepherdjerred.funsheet.storage;

import com.shepherdjerred.funsheet.objects.Activity;
import com.shepherdjerred.funsheet.objects.Location;
import com.shepherdjerred.funsheet.objects.Tag;
import com.shepherdjerred.funsheet.objects.Type;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface Store {

    void addActivity(Activity activity);
    Optional<Activity> getActivity(UUID uuid);
    Collection<Activity> getActivities();
    void deleteActivity(UUID uuid);
    void updateActivity(Activity activity);
    boolean isActivityNameTaken(String string);

    void addTag(Tag tag);
    Optional<Tag> getTag(UUID uuid);
    Collection<Tag> getTags();
    void deleteTag(UUID uuid);
    void updateTag(Tag tag);
    boolean isTagNameTaken(String string);

    void addType(Type type);
    Optional<Type> getType(UUID uuid);
    Collection<Type> getTypes();
    void deleteType(UUID uuid);
    void updateType(Type type);
    boolean isTypeNameTaken(String string);

    void addLocation(Location location);
    Optional<Location> getLocation(UUID uuid);
    Collection<Location> getLocations();
    void deleteLocation(UUID uuid);
    void updateLocation(Location location);
    boolean isLocationNameTaken(String string);

}
