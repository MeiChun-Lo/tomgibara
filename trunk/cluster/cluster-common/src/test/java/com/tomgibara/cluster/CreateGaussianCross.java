/*
 * Copyright 2007 Tom Gibara
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.tomgibara.cluster;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.math.random.GaussianRandomGenerator;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.NormalizedRandomGenerator;
import org.apache.commons.math.random.UncorrelatedRandomVectorGenerator;

public class CreateGaussianCross {

	public static void main(String[] args) throws IOException {
		GaussianRandomGenerator gen = new GaussianRandomGenerator(new JDKRandomGenerator());
		final double[] center = new double[] {0,0};
		int clusterSize = 300;
		FileWriter writer = new FileWriter("R/cross.txt");
		try {
			writeCluster(gen, center, new double[] {6, 1}, clusterSize, writer);
			writeCluster(gen, center, new double[] {1, 6}, clusterSize, writer);
		} finally {
			writer.close();
		}
	}

	private static void writeCluster(NormalizedRandomGenerator gen, double[] means, double[] deviations, int size, Writer writer) throws IOException {
		UncorrelatedRandomVectorGenerator c = new UncorrelatedRandomVectorGenerator(means, deviations, gen);
		for (int i = 0; i < size; i++) {
			double[] pt = c.nextVector();
			writer.write(String.format("%3.3f %3.3f%n", pt[0], pt[1]));
		}
	}
	
}
