package com.productcatalog.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.productcatalog.app.model.ApprovalQueue;

@Repository
public interface ApprovalQueueRepository extends JpaRepository<ApprovalQueue, Long> {
	List<ApprovalQueue> findAllByOrderByApprovalRequestDateAsc();

}
