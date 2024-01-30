package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;

import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

public class AccountingEventMessageVO implements Serializable {
    private String entityId;

    private AccountingNounEnum entity;
    
    private EventEnum event;

    public AccountingEventMessageVO(){}

    public AccountingNounEnum getEntity() {
        return entity;
    }

    public AccountingEventMessageVO setEntity(AccountingNounEnum entity) {
        this.entity = entity;
        return this;
    }

    public EventEnum getEvent() {
        return event;
    }

    public AccountingEventMessageVO setEvent(EventEnum event) {
        this.event = event;
        return this;
    }

    public String getEntityId() {
        return entityId;
    }

    public AccountingEventMessageVO setEntityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
        result = prime * result + ((entity == null) ? 0 : entity.hashCode());
        result = prime * result + ((event == null) ? 0 : event.hashCode());
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
        AccountingEventMessageVO other = (AccountingEventMessageVO) obj;
        if (entityId == null) {
            if (other.entityId != null)
                return false;
        } else if (!entityId.equals(other.entityId))
            return false;
        if (entity != other.entity)
            return false;
        if (event != other.event)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AccountingEventMessageVO [entityId=" + entityId + ", entity=" + entity + ", event=" + event + "]";
    }
        
}
