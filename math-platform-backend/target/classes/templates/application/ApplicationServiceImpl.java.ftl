package ${packageName}.application.service.impl;

import ${packageName}.application.service.${entityClass}ApplicationService;
import ${packageName}.domain.${entityKey}.entity.${entityClass};
import ${packageName}.domain.${entityKey}.service.${entityClass}DomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ${description}应用服务实现类
 * 
 * @author CodeGenerator
 * @since ${.now?string("yyyy-MM-dd")}
 */
@Service
@Slf4j
public class ${entityClass}ApplicationServiceImpl implements ${entityClass}ApplicationService {

    @Resource
    private ${entityClass}DomainService ${entityKey}DomainService;

    @Override
    public Long create${entityClass}(${entityClass} ${entityKey}) {
        ${entityKey}DomainService.create${entityClass}(${entityKey});
        return ${entityKey}.getId();
    }

    @Override
    public Boolean delete${entityClass}(Long id) {
        try {
            ${entityKey}DomainService.delete${entityClass}(id);
            return true;
        } catch (Exception e) {
            log.error("删除${entityName}失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    public Boolean update${entityClass}(${entityClass} ${entityKey}) {
        try {
            ${entityKey}DomainService.update${entityClass}(${entityKey});
            return true;
        } catch (Exception e) {
            log.error("更新${entityName}失败，ID: {}", ${entityKey}.getId(), e);
            return false;
        }
    }

    @Override
    public ${entityClass} get${entityClass}ById(Long id) {
        // TODO: 实现获取${entityName}的逻辑
        // 这里需要调用领域服务或仓储接口
        throw new UnsupportedOperationException("方法未实现");
    }
}
