/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package com.m2m.management.pecoff4j.util;

import com.m2m.management.pecoff4j.PE;
import com.m2m.management.pecoff4j.ResourceDirectory;
import com.m2m.management.pecoff4j.ResourceEntry;
import com.m2m.management.pecoff4j.io.DataReader;
import com.m2m.management.pecoff4j.io.DataWriter;
import com.m2m.management.pecoff4j.io.PEParser;
import com.m2m.management.pecoff4j.io.ResourceParser;
import com.m2m.management.pecoff4j.resources.IconImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IconExtractor {
	public static void extract(File var0, File var1, String var2) throws IOException {
		PE var3 = PEParser.parse(var0);
		ResourceDirectory var4 = var3.getImageData().getResourceTable();
		if (var4 != null) {
			com.m2m.management.pecoff4j.ResourceEntry[] var5 = com.m2m.management.pecoff4j.util.ResourceHelper.findResources(var4, 14);

			for(int var6 = 0; var6 < var5.length; ++var6) {
				com.m2m.management.pecoff4j.resources.GroupIconDirectory var7 = com.m2m.management.pecoff4j.resources.GroupIconDirectory.read(var5[var6].getData());
				com.m2m.management.pecoff4j.util.IconFile var8 = new IconFile();
				com.m2m.management.pecoff4j.resources.IconDirectory var9 = new com.m2m.management.pecoff4j.resources.IconDirectory();
				var9.setType(1);
				var9.setReserved(0);
				var8.setDirectory(var9);
				com.m2m.management.pecoff4j.resources.IconImage[] var10 = new com.m2m.management.pecoff4j.resources.IconImage[var7.getCount()];
				var8.setImages(var10);

				for(int var11 = 0; var11 < var7.getCount(); ++var11) {
					com.m2m.management.pecoff4j.resources.GroupIconDirectoryEntry var12 = var7.getEntry(var11);
					com.m2m.management.pecoff4j.resources.IconDirectoryEntry var13 = new com.m2m.management.pecoff4j.resources.IconDirectoryEntry();
					var13.copyFrom(var12);
					var9.add(var13);
					ResourceEntry[] var14 = ResourceHelper.findResources(var4, 3, var12.getId());
					if (var14 == null || var14.length != 1) {
						throw new IOException("Unexpected icons in resource file");
					}

					byte[] var15 = var14[0].getData();
					var13.setBytesInRes(var15.length);
					IconImage var16;
					if (var12.getWidth() == 0 && var12.getHeight() == 0) {
						var16 = com.m2m.management.pecoff4j.io.ResourceParser.readPNG(var15);
						var10[var11] = var16;
					} else {
						var16 = ResourceParser.readIconImage(new DataReader(var15), var12.getBytesInRes());
						var10[var11] = var16;
					}
				}

				File var17 = new File(var1, var2);
				com.m2m.management.pecoff4j.io.DataWriter var18 = new DataWriter(new FileOutputStream(var17));
				var8.write(var18);
				var18.close();
			}

		}
	}
}
