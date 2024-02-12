package com.lzh.controller;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.lzh.common.Result;
import com.lzh.constant.CodeConstant;
import com.lzh.entity.Reader;
import com.lzh.service.ReaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lizhihao
 * @since 2024-02-02
 */

@Api(tags = "读者接口")
@RestController
@RequestMapping("/reader")
public class ReaderController {

    @Autowired
    private ReaderService readerService;

    @ApiOperation("查询读者列表")
    @GetMapping("/list/API_001")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String phone,
                           @RequestParam(defaultValue = "") String nickname,
                           @RequestParam(defaultValue = "") String readerNumber) {
        return Result.success(readerService.readerListAPI_001(pageNum, pageSize, username, phone, nickname,readerNumber));
    }

    /**
     * 读者其实只能通过前端注册而存在，这个接口是为了扩展系统功能，也方便修改读者信息
     * @param reader
     * @return
     */
    @ApiOperation("新增或编辑读者")
    @PostMapping("/addOrUpdate/API_002")
    public Result addOrUpdateAPI_002(@RequestBody Reader reader) {
        if (StrUtil.isBlank(Convert.toStr(reader.getId())) && StrUtil.isBlank(reader.getPassword())) {
            reader.setPassword(SecureUtil.md5("123"));// 新增读者默认密码（新增时，未填写密码默认密码为123）
        }

        // username唯一（读者名唯一）
        // filter(o -> !Objects.equals(o.getId(), reader.getId())) 是为了排除当前这条记录，否则在修改的时候会提示读者已存在，修改失败
        Set<String> collect = readerService.list().stream().filter(o -> !Objects.equals(o.getId(), reader.getId())).map(Reader::getUsername).collect(Collectors.toSet());
        // 方式二：只需要判断list.size() > 0就说明读者名重复
//        List<Reader> list = readerService.list(new QueryWrapper<reader>().lambda()
//                .ne(StrUtil.isNotBlank(reader.getId().toString()), reader::getId, reader.getId())
//                .eq(reader::getUsername, reader.getUsername()));
        if (collect.contains(reader.getUsername())) {
            return Result.error(CodeConstant.CODE_400, "读者名已存在");
        }
        reader.setReaderNumber(reader.getUsername() + LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        return Result.success(readerService.saveOrUpdate(reader));
    }

    @ApiOperation("删除读者")
    @DeleteMapping("/delete/API_003/{id}")
    public Result deleteAPI_003(@PathVariable Integer id) {
        return Result.success(readerService.removeById(id));
    }

    @ApiOperation("批量删除读者")
    @PostMapping("/delete/batch/API_004")
    public Result deleteBatchAPI_004(@RequestBody List<Integer> ids) {
        return Result.success(readerService.removeByIds(ids));
    }

    @ApiOperation("导出读者")
    @GetMapping("/export/API_005")
    public void exportReaderAPI_005(HttpServletResponse response, @RequestParam String ids) {
        readerService.exportReaderAPI_005(response, ids);
    }

    @ApiOperation("根据读者id查询读者信息")
    @GetMapping("/getById/{id}")
    public Result getReaderById(@PathVariable String id) {
        return Result.success(readerService.getById(id));
    }

    @ApiOperation("启用账号开关")
    @PostMapping("/state/update")
    public Result update(@RequestBody Reader reader){
        readerService.updateById(reader);
        return Result.success();
    }

}

