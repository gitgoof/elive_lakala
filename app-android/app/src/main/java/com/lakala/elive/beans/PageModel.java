package com.lakala.elive.beans;

import java.io.Serializable;
import java.util.List;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/10/9 18:36
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class PageModel<T> implements Serializable {

    private static final long serialVersionUID = -4906400433425467744L;

    private long total;  //总条数

    private int pageNo ;//当前页

    private int nextPage; //下一页页数

    private int pageSize;//一页显示多少条

    private int totalPage;//总页数

    private List<T> rows;////当前页数据

    private int recordCount;//总记录数

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

}
