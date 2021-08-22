package me.bristermitten.warzone.leavemenu;

public interface LeaveRequeueMenuFactory {
    LeaveRequeueMenu create(Runnable action, String actionName);
}
