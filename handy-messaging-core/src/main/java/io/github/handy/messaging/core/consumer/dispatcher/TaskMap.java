/**
 * MIT License
 *
 * Copyright (c) 2024 Aron Sajan Philip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.handy.messaging.core.consumer.dispatcher;

import java.util.*;

/**
 * TaskMap class is a data structure that holds the tasks that are to be executed by the worker threads.
 * The tasks are grouped by transactionId and are stored in a queue.
 */
public class TaskMap {
    private Map<Optional<String>, Queue<WorkerTask>> workerTaskMap;

    public TaskMap(){
        this.workerTaskMap = new HashMap<>();
    }

    /**
     * Adds a task to the queue of tasks that are to be executed by the worker threads.
     * @param transactionId The transactionId of the task
     * @param task The task to be executed
     */
    public void addTransaction(Optional<String> transactionId, WorkerTask task){
        this.workerTaskMap.putIfAbsent(transactionId, new LinkedList<>());
        this.workerTaskMap.get(transactionId).add(task);
    }

    /**
     * Gets the next task that is to be executed by the worker threads.
     * @param transactionGroupId (Optional) The optional transactionId of the task
     * @return The next task that is to be executed
     */
    public WorkerTask getNextTask(Optional<String> transactionGroupId){
        if(this.workerTaskMap.containsKey(transactionGroupId)){
            WorkerTask nextTask = this.workerTaskMap.get(transactionGroupId).remove();
            if(this.workerTaskMap.get(transactionGroupId).isEmpty()){
                this.workerTaskMap.remove(transactionGroupId);
            }
            return nextTask;
        } else {
            throw new RuntimeException("Transaction Group Queue is empty");
        }
    }

    /**
     * Gets all the transactionIds that are present in the TaskMap
     * @return A list of all the transactionIds
     */
    public List<Optional<String>> getAllTransactions(){
        return new ArrayList<>(this.workerTaskMap.keySet());
    }

    /**
     * Checks if there are any tasks available for execution
     * @param transactionGroupId (Optional) The optional transactionId of the task
     * @return True if there are tasks available, False otherwise
     */
    public boolean hasAvailableTasks(Optional<String> transactionGroupId){
        if(this.workerTaskMap.containsKey(transactionGroupId)){
            return !this.workerTaskMap.get(transactionGroupId).isEmpty();
        } else {
            return false;
        }
    }

    /**
     * Gets the number of tasks that are available for execution
     * @param transactionId (Optional) The optional transactionId of the task
     * @return The number of tasks available for execution
     */
    public int getTaskCount(Optional<String> transactionId){
        if(this.workerTaskMap.containsKey(transactionId)){
            return this.workerTaskMap.get(transactionId).size();
        } else {
            return 0;
        }
    }
}
