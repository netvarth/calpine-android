package com.jaldeeinc.jaldee.utils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    LinearLayoutManager layoutManager;

    /**
     * Supporting only LinearLayoutManager for now.
     *
     * @param layoutManager
     */
    public PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
      //  Config.logV("totalItemCount"+totalItemCount);
       // Config.logV("toatal firstVisibleItemPosition"+firstVisibleItemPosition+visibleItemCount);
       // Config.logV("firstVisibleItemPosition ########"+firstVisibleItemPosition);

        //Config.logV("isLoading ########"+isLoading());
        if (!isLoading() /*&& !isLastPage()*/) {

           // Config.logV("totalItemCount"+totalItemCount);
         //   Config.logV("toatal firstVisibleItemPosition"+firstVisibleItemPosition+visibleItemCount);
          //  Config.logV("^$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+firstVisibleItemPosition );
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                   /* && totalItemCount >= getTotalPageCount()*/) {

              //  Config.logV("Load More items----------------------");
                loadMoreItems();
            }
        }

    }

    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

}
