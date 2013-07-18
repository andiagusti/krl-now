package org.mufid.krlnow.models;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mufid on 18/07/13.
 */
public class TrainStatusRawData implements Serializable {
    public String[][] aaData;

    public List<TrainStatusModel> fillListWithData(List<TrainStatusModel> list) {
        for (String[] row : aaData) {
            TrainStatusModel entry = new TrainStatusModel(Integer.parseInt(row[0]), row[1], row);
        }
        return list;
    }
}
