package com.orange.browser.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//import com.orange.obrowser.LogTag;

import junit.framework.Assert;

public class Invoker {
	static Object invoke(Object o, String name, Class[] params, Object[] values){
		Object ret = null;
		try {
			ret = o.getClass().getMethod(name, params).invoke(o, values);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			return printException(e);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			return printException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			return printException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			return printException(e);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			return printException(e);
		}
		return ret;		
	}

    public static Object getPrivateField(Object o, String fieldName) {
        // Check we have valid arguments... 
        Assert.assertNotNull(o);
        Assert.assertNotNull(fieldName);

        // Go and find the private field... 
        final Field fields[] = o.getClass().getSuperclass().getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if (fieldName.equals(fields[i].getName())) {
                try {
                    fields[i].setAccessible(true);
                    return fields[i].get(o);
                } catch (IllegalAccessException ex) {
                    Assert.fail("IllegalAccessException accessing " + fieldName);
                }
            }
        }
        Assert.fail("Field '" + fieldName + "' not found");
        return null;
    }

    public static Object invokePrivateMethod(Object o, String methodName, Class[] params, Object[] values) {
        // Check we have valid arguments... 
        Assert.assertNotNull(o);
        Assert.assertNotNull(methodName);
        Assert.assertNotNull(params);
        Assert.assertNotNull(values);

        // Go and find the private method... 
        try {
        	final Method method = o.getClass().getDeclaredMethod(methodName, params);
        	method.setAccessible(true);        	
        	return method.invoke(o, values);
        }catch (Exception e){
        }
        Assert.fail("Method '" + methodName + "' not found");
        return null;
    }
    
    public static Object setPrivateField(Object o, String fieldName,int code) {
        // Check we have valid arguments... 
        Assert.assertNotNull(fieldName);

        // Go and find the private field... 
        final Field fields[] = o.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if (fieldName.equals(fields[i].getName())) {
                try {
                    fields[i].setAccessible(true);
                    fields[i].setInt(o, code);
                    return fields[i].get(o) ;
                } catch (IllegalAccessException ex) {
                    Assert.fail("IllegalAccessException accessing " + fieldName);
                }
            }
        }
        Assert.fail("Field '" + fieldName + "' not found");
        return null;
    }
	static Object printException(Exception e){
		e.printStackTrace();
		return null;
	}
}
