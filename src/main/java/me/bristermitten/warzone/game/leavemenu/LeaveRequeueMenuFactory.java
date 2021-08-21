package me.bristermitten.warzone.game.leavemenu;

public interface LeaveRequeueMenuFactory {
    LeaveRequeueMenu create(Runnable action, String actionName);
}
