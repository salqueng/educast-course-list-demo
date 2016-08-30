package com.example.hoon.educastcourselistdemo.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.android.volley.Request;
import com.example.hoon.educastcourselistdemo.utils.reflect.Cloner;
import com.example.hoon.educastcourselistdemo.utils.reflect.MultipleParameterizedType;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hoon on 15. 1. 8..
 */
public class RestManager {
    public static final int CHANGE_TYPE_CREATE = 1;
    public static final int CHANGE_TYPE_READ = 2;
    public static final int CHANGE_TYPE_UPDATE = 3;
    public static final int CHANGE_TYPE_DELETE = 4;

    public static final String KEY_DEFAULT = "DEFAULT";

    private static final Map<String, RestManager> managerMap = new HashMap<>();

    public static RestManager get(Context ctx, String key) {
        if (managerMap.containsKey(key)) {
            return managerMap.get(key);
        } else {
            RestManager result = new RestManager(ctx.getApplicationContext());
            managerMap.put(key, result);
            return result;
        }
    }

    public static RestManager get(Context ctx) {
        return get(ctx, KEY_DEFAULT);
    }

    private Map<String,
            List<Pair<WeakReference<OnChangeObjectListener<IRestIdentifiable>>, Handler>>>
            mUpdateListenerMap = new HashMap<>();

    private LruCache<String, IRestIdentifiable> mObjectCache = new LruCache<String, IRestIdentifiable>(1000) {
        @Override
        protected int sizeOf(String key, IRestIdentifiable value) {
            return super.sizeOf(key, value);
        }
    };

    private Map<String, String> mIdentifierAliasMap = new HashMap<>();

    private OnBeforeSendingRequestListener mBeforeCallback;
    private Map<String, String> mHeaders;
    private VolleyManager mVolleyManager;

    private RestManager(Context ctx) {
        mVolleyManager = VolleyManager.get(ctx);
        mHeaders = new HashMap<>();
    }

    public RestManager setHeader(String key, String value) {
        mHeaders.put(key, value);
        return this;
    }

    public RestManager removeHeader(String key) {
        mHeaders.remove(key);
        return this;
    }

    public String getHeader(String key) {
        return mHeaders.get(key);
    }

    public <U, T> RestRequestFuture<T> request(
            int method, String url, U obj, Type requestType,
            Type responseType, Map<String, String> headers
    ) {
        if (mBeforeCallback != null) {
            mBeforeCallback.onBeforeSendingRequest(this);
        }

        final HashMap<String, String> newHeaders = new HashMap<>(mHeaders);
        if (headers != null) {
            newHeaders.putAll(headers);
        }

        final RestRequestFuture<T> future = RestRequestFuture.newFuture();
        final RestRequest<U, T> request = new RestRequest<>(
                method, url, obj, requestType, responseType, newHeaders, future
        );
        request.setRestManager(this);
        future.setRequest(mVolleyManager.getRequestQueue().add(request));
        return future;
    }

    public <T> RestRequestFuture<T> create(String url, T obj, Map<String, String> header) {
        Type tType = TypeToken.get(obj.getClass()).getType();
        return request(Request.Method.POST, url, obj, tType, tType, header);
    }

    public <T> RestRequestFuture<T> create(String url, T obj) {
        return create(url, obj, null);
    }

    public <T> RestRequestFuture<T> readAsItem(String url, Class<T> klass, Map<String, String> header) {
        Type tType = TypeToken.get(klass).getType();
        return request(Request.Method.GET, url, null, tType, tType, header);
    }

    public <T> RestRequestFuture<T> readAsItem(String url, Class<T> klass) {
        return readAsItem(url, klass, null);
    }

    public <T> RestRequestFuture<List<T>> readAsList(String url, Class<T> klass, Map<String, String> header) {
        Type requestType = TypeToken.get(klass).getType();
        Type responseType = new MultipleParameterizedType(ArrayList.class, klass);
        return request(Request.Method.GET, url, null, requestType, responseType, header);
    }

    public <T> RestRequestFuture<List<T>> readAsList(String url, Class<T> klass) {
        return readAsList(url, klass, null);
    }

    public <T> RestRequestFuture<QueryResult<T>> readAsQueryResult(String url, Class<T> klass, Map<String, String> header) {
        Type requestType = TypeToken.get(klass).getType();
        Type responseType = new MultipleParameterizedType(QueryResult.class, klass);
        return request(Request.Method.GET, url, null, requestType, responseType, header);
    }

    public <T> RestRequestFuture<QueryResult<T>> readAsQueryResult(String url, Class<T> klass) {
        return readAsQueryResult(url, klass, null);
    }

    public <T> RestRequestFuture<T> update(String url, T obj, Map<String, String> header) {
        Type tType = TypeToken.get(obj.getClass()).getType();
        return request(Request.Method.PUT, url, obj, tType, tType, header);
    }

    public <T> RestRequestFuture<T> update(String url, T obj) {
        return update(url, obj, null);
    }

    public <T> RestRequestFuture<T> delete(String url, Class<T> klass, Map<String, String> header) {
        Type tType = TypeToken.get(klass).getType();
        return request(Request.Method.DELETE, url, null, tType, tType, header);
    }

    public <T> RestRequestFuture<T> delete(String url, Class<T> klass) {
        return delete(url, klass, null);
    }

    public RestManager setOnBeforeSendingRequestListener (OnBeforeSendingRequestListener listener) {
        mBeforeCallback = listener;
        return this;
    }

    public <T extends IRestIdentifiable> void registerOnChangeObjectListener(
            String identifierOrAlias, boolean isUnique, OnChangeObjectListener<T> listener, Handler handler
    ) {
        String identifier = getIdentifierFromAlias(identifierOrAlias);

        List<Pair<WeakReference<OnChangeObjectListener<IRestIdentifiable>>, Handler>> listListener;
        if (mUpdateListenerMap.containsKey(identifier)){
            if(isUnique){
                return;
            } else {
                listListener = mUpdateListenerMap.get(identifier);
            }
        } else {
            listListener = new ArrayList<>();
            mUpdateListenerMap.put(identifier, listListener);
        }

        WeakReference<OnChangeObjectListener<IRestIdentifiable>> listenerReference =
                new WeakReference<>((OnChangeObjectListener<IRestIdentifiable>) listener);

        listListener.add(new Pair<>(listenerReference, handler));
    }

    public <T extends IRestIdentifiable> void registerOnChangeObjectListener(
            String identifierOrAlias, OnChangeObjectListener<T> listener, Handler handler
    ) {
        registerOnChangeObjectListener(identifierOrAlias, false, listener, handler);
    }

    public <T extends IRestIdentifiable> void notifyObjectChange(final T obj, final int method) {
        String identifier = obj.getIdentifier();
        // Add To Object Cache
        mObjectCache.put(identifier, obj);

        if (mUpdateListenerMap.containsKey(identifier)) {
            List<Pair<WeakReference<OnChangeObjectListener<IRestIdentifiable>>, Handler>> list =
                    mUpdateListenerMap.get(identifier);
            Iterator<Pair<WeakReference<OnChangeObjectListener<IRestIdentifiable>>, Handler>>
                    iterator = list.iterator();

            while (iterator.hasNext()) {
                Pair<WeakReference<OnChangeObjectListener<IRestIdentifiable>>, Handler>
                        pair = iterator.next();
                WeakReference<OnChangeObjectListener<IRestIdentifiable>> listenerRef = pair.first;

                if (listenerRef.get() == null) {
                    // Null CourseReference -> Remove it!
                    iterator.remove();
                } else {
                    final OnChangeObjectListener<T> listener =
                            (OnChangeObjectListener<T>) listenerRef.get();
                    Handler handler = pair.second;
                    if (handler == null) {
                        handler = new Handler(Looper.getMainLooper());
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onChangeObject(RestManager.this, obj, method);
                        }
                    });
                }

            }
        }
    }

    public <T extends IRestIdentifiable> T fetchFromCache(String identifier, Class<T> klass) {

        if (mIdentifierAliasMap.containsKey(identifier)) {
            identifier = mIdentifierAliasMap.get(identifier);
        }

        IRestIdentifiable obj = mObjectCache.get(identifier);

        if (obj == null) {
            return null;
        } else {
            if (klass.isInstance(obj)) {
                return (T) obj;
            } else {
                return null;
            }
        }
    }

    public void setIdentifierAlias(String alias, String identifier) {
        mIdentifierAliasMap.put(alias, identifier);
    }

    public String getIdentifierFromAlias(String alias) {
        if (mIdentifierAliasMap.containsKey(alias)) {
            return mIdentifierAliasMap.get(alias);
        } else {
            return alias;
        }
    }

    public void removeIdentifierAlias(String alias) {
        if (mIdentifierAliasMap.containsKey(alias)) {
            mIdentifierAliasMap.remove(alias);
        }
    }

    public Object updateAndGetManagedObject(Object obj, int method) {
        return updateAndGetManagedObject(obj, method, new ArrayList<String>(), new ArrayList<Integer>());
    }

    private Object updateAndGetManagedObject(Object obj, int method, List<String> excludeFieldList, List<Integer> considered) {
        if (obj == null) {
            return null;
        } else if (obj.getClass().isPrimitive()) {
            return obj;
        } else if (considered.contains(obj.hashCode())) {
            // Already Checked
            return obj;
        } else {
            // Mark that obj is checked
            considered.add(obj.hashCode());
        }

        if (obj instanceof IRestRefinable) {
            ((IRestRefinable) obj).refine();
        }

        if (obj instanceof List) {
            List listedObj = (List) obj;
            for (int i = 0; i < listedObj.size(); i += 1) {
                // propagate excludeFieldList to each items
                Object innerObj = updateAndGetManagedObject(listedObj.get(i), method, excludeFieldList, considered);
                listedObj.set(i, innerObj);
            }
        } else {
            for (Field field: obj.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    Object inner = field.get(obj);
                    Exclude excludeConf = field.getAnnotation(Exclude.class);
                    Include includeConf = field.getAnnotation(Include.class);

                    final List<String> newExcludeFieldList;
                    if (excludeConf != null) {
                        String rawFields = excludeConf.fields();
                        String separator = excludeConf.separator();

                        String[] excludeFields = rawFields.split(separator);
                        newExcludeFieldList = Arrays.asList(excludeFields);
                    } else if (includeConf != null) {
                        String rawFields = includeConf.fields();
                        String separator = includeConf.separator();
                        String[] includeFields  = rawFields.split(separator);

                        List<String> includeFieldList = Arrays.asList(includeFields);
                        newExcludeFieldList = new ArrayList<>();

                        for (Field inField: field.getDeclaringClass().getDeclaredFields()) {
                            if (! includeFieldList.contains(inField.getName())) {
                                newExcludeFieldList.add(inField.getName());
                            }
                        }

                    } else {
                        newExcludeFieldList = new ArrayList<>();
                    }

                    field.set(obj, updateAndGetManagedObject(inner, method, newExcludeFieldList, considered));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        // IRestIdentifiable Check?
        if (obj instanceof IRestIdentifiable) {
            IRestIdentifiable identifiableObj = (IRestIdentifiable) obj;

            if (TextUtils.isEmpty(identifiableObj.getIdentifier())) {
                // No Hope - No matching case
                return obj;
            }

            IRestIdentifiable oldIdentifiableObj = mObjectCache.get(identifiableObj.getIdentifier());

            if (oldIdentifiableObj == null) {

                if (excludeFieldList.size() == 0) {
                    mObjectCache.put(identifiableObj.getIdentifier(), identifiableObj);
                    oldIdentifiableObj = identifiableObj;
                } else {
                    // Cannot do something without excluded IRestIdentifiable Object
                    // Do not register to the cache, just return the object
                    return identifiableObj;
                }
            } else {
                // Copy To Old Object exclude some fields
                Cloner.copyFieldByField(identifiableObj, oldIdentifiableObj, excludeFieldList);
            }
            // Notify Update Of Object
            notifyObjectChange(oldIdentifiableObj, method);
            return oldIdentifiableObj;
        } else {
            return obj;
        }
    }

    public void clearCache() {
        mObjectCache.evictAll();
        mIdentifierAliasMap.clear();
    }

    public interface OnChangeObjectListener<T extends IRestIdentifiable> {
        void onChangeObject(RestManager manager, T obj, int updateType);
    }

    public interface OnBeforeSendingRequestListener {
        void onBeforeSendingRequest(RestManager manager);
    }
}
