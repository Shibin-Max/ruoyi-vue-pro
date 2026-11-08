package cn.iocoder.yudao.module.system.service.dept;

import cn.iocoder.yudao.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.mysql.dept.DeptMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomCommonStatus;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link DeptServiceImpl} 的 Testcontainers 集成测试类
 *
 * 场景与 {@link DeptServiceImplTest#testCreateDept()} 完全相同（新增部门后回查校验），
 * 区别仅在于底层数据库：DeptServiceImplTest 走 H2 内存库，本类走真 MySQL 8 容器。
 * 两边都跑通且结果一致，才说明业务逻辑与具体数据库无关。
 */
@Import(DeptServiceImpl.class)
public class DeptServiceImplTestcontainersTest extends BaseDbTestcontainersTest {

    @Resource
    private DeptServiceImpl deptService;
    @Resource
    private DeptMapper deptMapper;

    @Test
    public void testCreateDept() {
        // 准备参数
        DeptSaveReqVO reqVO = randomPojo(DeptSaveReqVO.class, o -> {
            o.setId(null); // 防止 id 被设置
            o.setParentId(DeptDO.PARENT_ID_ROOT);
            o.setStatus(randomCommonStatus());
        });

        // 调用
        Long deptId = deptService.createDept(reqVO);
        // 断言
        assertNotNull(deptId);
        // 校验记录的属性是否正确
        DeptDO deptDO = deptMapper.selectById(deptId);
        assertPojoEquals(reqVO, deptDO, "id");
    }

    @Test
    public void testInsertAndSelectDept() {
        // mock 数据：直接通过 Mapper 插入一条部门记录（增）
        DeptDO dbDeptDO = randomPojo(DeptDO.class, o -> o.setStatus(randomCommonStatus()));
        deptMapper.insert(dbDeptDO);

        // 调用：按编号回查（查）
        DeptDO deptDO = deptMapper.selectById(dbDeptDO.getId());
        // 断言
        assertNotNull(deptDO);
        assertPojoEquals(dbDeptDO, deptDO);
    }

}
