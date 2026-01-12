package com.example.mockodsvue.service;

import com.example.mockodsvue.model.dto.BranchProductListDTO;
import com.example.mockodsvue.model.entity.branch.BranchProductList;
import com.example.mockodsvue.repository.BranchProductListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchProductListService {

    private final BranchProductListRepository branchProductListRepository;

    public List<BranchProductList> getBranchProductList(String branchCode) {
        return null;
    }

    public List<BranchProductList> create(List<BranchProductListDTO> list) {
        return null;
    }

    public List<BranchProductList> update(List<BranchProductListDTO> list) {
        return null;
    }

    public List<BranchProductList> delete(List<BranchProductListDTO> list) {
        return null;
    }

    public List<BranchProductList> copy(String fromBranchCode, String toBranchCode) {
        return null;
    }

    private List<BranchProductList> exist(String branchCode) {
        return null;
    }

}
