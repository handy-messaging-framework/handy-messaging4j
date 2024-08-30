/**
 * MIT License
 *
 * Copyright (c) 2024 Aron Sajan Philip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.handy.messaging.interfaces;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

/**
 * Interface presenting each field in the method
 */
public interface MessageField {

    /**
     * Function to get the field getter method
     * @return String represeting the field getter method
     */
    String getFieldGetterMethod();

    /**
     * Function to get the field comparator inorder to compare similar fields in different messages of the same type
     * @return Comparator for the field
     */
    Comparator getFieldComparator();

    /**
     * Function to compare two messages based on the field
     * @param messageObjX - Message object 1
     * @param messageObjY - Message object 2
     * @return boolean representing if the field values are equal
     */
    default boolean compare(Message messageObjX, Message messageObjY){
        if(messageObjX.getClass().equals(messageObjY.getClass())){
            try {
                Object xVal = messageObjX.getClass().getMethod(this.getFieldGetterMethod()).invoke(messageObjX);
                Object yVal = messageObjY.getClass().getMethod(this.getFieldGetterMethod()).invoke(messageObjY);
                return this.getFieldComparator().compare(xVal, yVal)==0;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex){
                throw new RuntimeException(ex);
            }
        }
        return false;
    }
}
