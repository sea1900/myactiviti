package me.kafeitu.demo.activiti.dao;

import me.kafeitu.demo.activiti.entity.oa.Leave;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * 请假实体管理接口
 *
 * @author hc
 */
@Component
public interface LeaveDao extends CrudRepository<Leave, Long> {
}
