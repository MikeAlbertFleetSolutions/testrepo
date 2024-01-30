package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.util.Date;

import com.mikealbert.util.data.DateUtil;

public class NoteVO implements Serializable {    
    private Date createDate;

    private String comment;

    public Date getCreateDate() {
        return DateUtil.clone(createDate);
    }

    public NoteVO setCreateDate(Date createDate) {
        this.createDate = DateUtil.clone(createDate);
        return this;
    }

    public String getComment() {
        return comment;
    }

    public NoteVO setComment(String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NoteVO other = (NoteVO) obj;
        if (comment == null) {
            if (other.comment != null)
                return false;
        } else if (!comment.equals(other.comment))
            return false;
        if (createDate == null) {
            if (other.createDate != null)
                return false;
        } else if (!createDate.equals(other.createDate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "NoteVO [comment=" + comment + ", createDate=" + createDate + "]";
    }
    

    
}
