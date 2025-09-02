package ${packageName}.infrastructure.repository.impl;

import ${packageName}.domain.${entityKey}.entity.${entityClass};
import ${packageName}.domain.${entityKey}.repository.${entityClass}Repository;
import ${packageName}.infrastructure.mapper.${entityClass}Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * ${description}仓储实现类
 * 
 * @author CodeGenerator
 * @since ${.now?string("yyyy-MM-dd")}
 */
@Repository
@Slf4j
public class ${entityClass}RepositoryImpl implements ${entityClass}Repository {

    @Resource
    private ${entityClass}Mapper ${entityKey}Mapper;

    @Override
    public ${entityClass} save(${entityClass} ${entityKey}) {
        int result = ${entityKey}Mapper.insert(${entityKey});
        if (result > 0) {
            return ${entityKey};
        }
        return null;
    }

    @Override
    public ${entityClass} findById(Long id) {
        return ${entityKey}Mapper.selectById(id);
    }

    @Override
    public Boolean updateById(${entityClass} ${entityKey}) {
        int result = ${entityKey}Mapper.updateById(${entityKey});
        return result > 0;
    }

    @Override
    public Boolean removeById(Long id) {
        int result = ${entityKey}Mapper.deleteById(id);
        return result > 0;
    }
}
