package com.snda.storage.service.multi.cs;

import com.snda.storage.service.model.CSObject;
import com.snda.storage.service.model.MultipartCompleted;
import com.snda.storage.service.model.MultipartUpload;
import com.snda.storage.service.multi.ThreadWatcher;
import com.snda.storage.service.multi.event.ServiceEvent;


/**
 * Multi-threaded service event fired by
 * {@link ThreadedCSService#multipartCompleteUploads(java.util.List)}.
 * <p>
 * EVENT_IN_PROGRESS events include an array of the {@link CSObject}s that have been created
 * since the last progress event was fired. These objects are available via
 * {@link #getCompletedUploads()}.
 * <p>
 * EVENT_CANCELLED events include an array of the {@link CSObject}s that had not been created
 * before the operation was cancelled. These objects are available via
 * {@link #getCancelledUploads()}.
 *
 */
public class MultipartCompletesEvent extends ServiceEvent {
	
	private MultipartUpload[] incompleteUploads = null;
    private MultipartCompleted[] completedUploads = null;

	protected MultipartCompletesEvent(int eventCode, Object uniqueOperationId) {
		super(eventCode, uniqueOperationId);
	}
	
	public static MultipartCompletesEvent newErrorEvent(Throwable t, Object uniqueOperationId) {
        MultipartCompletesEvent event = new MultipartCompletesEvent(EVENT_ERROR, uniqueOperationId);
        event.setErrorCause(t);
        return event;
    }

    public static MultipartCompletesEvent newStartedEvent(ThreadWatcher threadWatcher, Object uniqueOperationId) {
        MultipartCompletesEvent event = new MultipartCompletesEvent(EVENT_STARTED, uniqueOperationId);
        event.setThreadWatcher(threadWatcher);
        return event;
    }

    public static MultipartCompletesEvent newInProgressEvent(ThreadWatcher threadWatcher,
        MultipartCompleted[] completedUploads, Object uniqueOperationId)
    {
        MultipartCompletesEvent event = new MultipartCompletesEvent(EVENT_IN_PROGRESS, uniqueOperationId);
        event.setThreadWatcher(threadWatcher);
        event.setCompleteUploads(completedUploads);
        return event;
    }

    public static MultipartCompletesEvent newCompletedEvent(Object uniqueOperationId) {
        MultipartCompletesEvent event = new MultipartCompletesEvent(EVENT_COMPLETED, uniqueOperationId);
        return event;
    }

    public static MultipartCompletesEvent newCancelledEvent(MultipartUpload[] incompletedUploads,
        Object uniqueOperationId)
    {
        MultipartCompletesEvent event = new MultipartCompletesEvent(EVENT_CANCELLED, uniqueOperationId);
        event.setIncompleteUploads(incompletedUploads);
        return event;
    }

    public static MultipartCompletesEvent newIgnoredErrorsEvent(ThreadWatcher threadWatcher,
        Throwable[] ignoredErrors, Object uniqueOperationId)
    {
        MultipartCompletesEvent event = new MultipartCompletesEvent(EVENT_IGNORED_ERRORS, uniqueOperationId);
        event.setIgnoredErrors(ignoredErrors);
        return event;
    }


    private void setIncompleteUploads(MultipartUpload[] uploads) {
        this.incompleteUploads = uploads;
    }

    private void setCompleteUploads(MultipartCompleted[] completed) {
        this.completedUploads = completed;
    }

    /**
     * @return
     * the {@link MultipartUpload}s that have been completed since the last progress event was fired.
     * @throws IllegalStateException
     * created objects are only available from EVENT_IN_PROGRESS events.
     */
    public MultipartCompleted[] getCompletedUploads() throws IllegalStateException {
        if (getEventCode() != EVENT_IN_PROGRESS) {
            throw new IllegalStateException("Started Objects are only available from EVENT_IN_PROGRESS events");
        }
        return completedUploads;
    }

    /**
     * @return
     * the {@link MultipartUpload}s that were not completed before the operation was cancelled.
     * @throws IllegalStateException
     * cancelled objects are only available from EVENT_CANCELLED events.
     */
    public MultipartUpload[] getCancelledUploads() throws IllegalStateException {
        if (getEventCode() != EVENT_CANCELLED) {
            throw new IllegalStateException("Cancelled Objects are  only available from EVENT_CANCELLED events");
        }
        return incompleteUploads;
    }

}
