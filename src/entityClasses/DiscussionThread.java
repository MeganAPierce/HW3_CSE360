package entityClasses;

import java.time.LocalDateTime;
/**
 * <p>Represents a discussion thread in the student discussion forum.  
 * This class stores the core information required for a staff-created thread, 
 * including the thread name, contained posts, and deletion.</p>
 * 
 * <p>The post class supports the user stories by providing the data and operations 
 * needed to create posts, edit posts, group posts into threads, determine authorship,
 * and soft delete posts without permanently removing them from the database. Soft deletion preserves
 * the record for later review, testing and possible staff-side review.</p>
 */

public class DiscussionThread {
	
	/**
     * Name of the thread.
     * <p>This is used to track the thread name, subject to updating and deletion.</p>*/
		private String name;
		/**
	     * Array of posts in thread.
	     * <p>This is used to store all posts associated with the thread in an array item.</p>*/
	    private Post[] posts;
	    /**
	     * Deletion status.
	     * <p>This is used to indicate whether the thread has been soft deleted.</p>*/
	    private boolean deleted;
	    /**
	     * Deletion time stamp.
	     * <p>This is used to track the time at which a thread was deleted.</p>*/
		private LocalDateTime deletedAt;
		
		/**
	     * This constructs a new default/unused thread.
	     * 
	     * <p> This constructor is used when a staff user creates a new thread through the discussion system. 
	     * It initializes the thread with the provided name, creates the array of posts,
	     * and marks the thread as not deleted.</p>
	     * 
	     * @param name thread's name
	     * */
		public DiscussionThread() {
			this.name = "[ADD NAME]";
			posts = new Post[100];
			deleted = false;
		}
		
		/**
	     * This constructs a new thread.
	     * 
	     * <p> This constructor is used when a staff user creates a new thread through the discussion system. 
	     * It initializes the thread with the provided name, creates the array of posts,
	     * and marks the thread as not deleted.</p>
	     * 
	     * @param name thread's name
	     * */
		public DiscussionThread(String name) {
			this.name = name;
			posts = new Post[100];
			deleted = false;
		}
		
		/**
	     * Updates the size of the post array to contain more posts.
	     * 
	     * <p>This method resizes the array of posts associated with the thread to 
	     * accommodate for a greater number of posts.</p>
	     * 
	     */
		public void resize() {
			Post[] resized = new Post[posts.length*2];
			for (int i = 0; i<posts.length; i++) {
				resized[i] = posts[i];
			}
			posts = resized;
		}
		
		/**
	     * Updates the name of a thread.
	     * 
	     * <p>This method supports the student user story that allows staff users to 
	     * modify existing threads' names. Only the thread name may be updated. 
	     * If the post has been deleted it will not be updated.</p>
	     * 
	     *
	     * @param newThreadName new thread name to assign if not blank
	     * @param newTitle new title to assign if not null
	     * @param newBody new message to assign if not null
	     * @throws IllegalStateException if the post has already been deleted
	     */
		public void updateThread(String threadName) {
			if (deleted)
	            throw new IllegalStateException("Cannot update a deleted thread.");

	        if (threadName != null)
	            name = threadName.trim();

		}
		
		/**
		    * Returns the name of the thread.
		    *
		    * @return the name
		    */
		public String getThreadName() { return name; }
		
		/**
		    * Returns the array of posts in the thread.
		    *
		    * @return the posts
		    */
		public Post[] getPosts() { return posts; }
		
		/**
		    * Returns the deletion status.
		    *
		    * @return deleted
		    */
		public boolean isDeleted() { return deleted; }
		
		/**
		    * Returns the time of deletion.
		    *
		    * @return the deletedAt timestamp
		    */
		public LocalDateTime getDeletedAt() { return deletedAt; }
		
		/**
	     * This method soft deletes a thread.
	     * <p>This method does not remove the thread object from the system entirely, but 
	     * it marks the thread as "Deleted" and keeps the records. This supports delete behavior
	     * while preserving the information for later review if necessary.</p>
	     */
	    public void softDelete() {
	        if (!deleted) {
	            deleted = true;
	            deletedAt = LocalDateTime.now();
	            name = "[DELETED]";
	        }
	    }
		
	}