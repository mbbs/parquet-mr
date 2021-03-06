/**
 * Copyright 2012 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package parquet.column.values.rle;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Alex Levenson
 */
public class RunLengthBitPackingHybridIntegrationTest {

  @Test
  public void integrationTest() throws Exception {
    for (int i = 0; i <= 32; i++) {
      doIntegrationTest(i);
    }
  }

  private void doIntegrationTest(int bitWidth) throws Exception {
    long modValue = 1L << bitWidth;

    RunLengthBitPackingHybridEncoder encoder = new RunLengthBitPackingHybridEncoder(bitWidth, 1000);

    for (int i = 0; i < 100; i++) {
      encoder.writeInt((int) (i % modValue));
    }

    for (int i = 0; i < 100; i++) {
      encoder.writeInt((int) (77 % modValue));
    }

    for (int i = 0; i < 100; i++) {
      encoder.writeInt((int) (88 % modValue));
    }

    for (int i = 0; i < 1000; i++) {
      encoder.writeInt((int) (i % modValue));
      encoder.writeInt((int) (i % modValue));
      encoder.writeInt((int) (i % modValue));
    }

    for (int i = 0; i < 1000; i++) {
      encoder.writeInt((int) (17 % modValue));
    }

    InputStream in = new ByteArrayInputStream(encoder.toBytes().toByteArray());

    RunLengthBitPackingHybridDecoder decoder = new RunLengthBitPackingHybridDecoder(bitWidth, in);

    for (int i = 0; i < 100; i++) {
      assertEquals(i % modValue, decoder.readInt());
    }

    for (int i = 0; i < 100; i++) {
      assertEquals(77 % modValue, decoder.readInt());
    }

    for (int i = 0; i < 100; i++) {
      assertEquals(88 % modValue, decoder.readInt());
    }

    for (int i = 0; i < 1000; i++) {
      assertEquals(i % modValue, decoder.readInt());
      assertEquals(i % modValue, decoder.readInt());
      assertEquals(i % modValue, decoder.readInt());
    }

    for (int i = 0; i < 1000; i++) {
      assertEquals(17 % modValue, decoder.readInt());
    }
  }
}
