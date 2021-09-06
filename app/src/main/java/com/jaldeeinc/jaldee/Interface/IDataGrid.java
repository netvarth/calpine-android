package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.model.DataGrid;

public interface IDataGrid {

    void onEditClick(DataGrid gridObj, int position);

    void onDeleteClick(int position);
}
