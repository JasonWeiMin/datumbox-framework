/**
 * Copyright (C) 2013-2015 Vasilis Vryniotis <bbriniotis at datumbox.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datumbox.framework.machinelearning.classification;

import com.datumbox.common.dataobjects.Dataset;
import com.datumbox.common.dataobjects.Record;
import com.datumbox.common.persistentstorage.interfaces.DatabaseConfiguration;
import com.datumbox.common.utilities.RandomValue;
import com.datumbox.configuration.TestConfiguration;
import com.datumbox.tests.utilities.Datasets;
import com.datumbox.tests.utilities.TestUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 */
public class MaximumEntropyTest {
    
    public MaximumEntropyTest() {
    }


    /**
     * Test of predict method, of class MaximumEntropy.
     */
    @Test
    public void testValidate() {
        TestUtils.log(this.getClass(), "validate");
        RandomValue.setRandomGenerator(new Random(TestConfiguration.RANDOM_SEED));
        DatabaseConfiguration dbConf = TestUtils.getDBConfig();
        
        
        Dataset[] data = Datasets.carsNumeric(dbConf);
        
        Dataset trainingData = data[0];
        Dataset validationData = data[1];
        
        
        String dbName = "JUnitClassifier";
        MaximumEntropy instance = new MaximumEntropy(dbName, dbConf);
        
        MaximumEntropy.TrainingParameters param = new MaximumEntropy.TrainingParameters();
        param.setTotalIterations(10);
        
        instance.fit(trainingData, param);
        
        
        instance = null;
        instance = new MaximumEntropy(dbName, dbConf);
        
        instance.validate(validationData);
        
        Map<Integer, Object> expResult = new HashMap<>();
        Map<Integer, Object> result = new HashMap<>();
        for(Integer rId : validationData) {
            Record r = validationData.get(rId);
            expResult.put(rId, r.getY());
            result.put(rId, r.getYPredicted());
        }
        assertEquals(expResult, result);
        
        instance.erase();
    }


    /**
     * Test of kFoldCrossValidation method, of class MaximumEntropy.
     */
    @Test
    public void testKFoldCrossValidation() {
        TestUtils.log(this.getClass(), "kFoldCrossValidation");
        RandomValue.setRandomGenerator(new Random(TestConfiguration.RANDOM_SEED));
        DatabaseConfiguration dbConf = TestUtils.getDBConfig();
        
        int k = 5;
        
        Dataset trainingData = Datasets.carsNumeric(dbConf)[0];
        
        
        String dbName = "JUnitClassifier";
        MaximumEntropy instance = new MaximumEntropy(dbName, dbConf);
        
        MaximumEntropy.TrainingParameters param = new MaximumEntropy.TrainingParameters();
        param.setTotalIterations(10);
        
        MaximumEntropy.ValidationMetrics vm = instance.kFoldCrossValidation(trainingData, param, k);
        
        double expResult = 0.6051098901098901;
        double result = vm.getMacroF1();
        assertEquals(expResult, result, TestConfiguration.DOUBLE_ACCURACY_HIGH);
        instance.erase();
    }

    
}
