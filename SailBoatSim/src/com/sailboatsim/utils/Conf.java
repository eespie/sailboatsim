/**
 * created              : 20 dec. 2008
 * copyright            : (C) 2008 by Eric Espie, Patrice Espie
 * version              : $Id$
 */

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package com.sailboatsim.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.thoughtworks.xstream.XStream;

/**
 */
public class Conf<T> {

    @SuppressWarnings("unchecked")
    public T load(String pSection, String pFile) {
        XStream xStream = new XStream();

        FileInputStream fis;
        try {
            fis = new FileInputStream(Utils.getFilePath(pSection, pFile));
        } catch (Exception e) {
            // Default values
            return null;
        }
        return (T) xStream.fromXML(fis);
    }

    public void save(T pObj, String pSection, String pFile) {
        XStream xStream = new XStream();

        // Write to a file in the file system
        try {
            FileOutputStream fos = new FileOutputStream(Utils.getFilePath(pSection, pFile));
            xStream.toXML(pObj, fos);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
