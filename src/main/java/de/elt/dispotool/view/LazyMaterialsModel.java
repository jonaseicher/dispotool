///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.elt.dispotool.view;
//
//import de.elt.dispotool.dao.MaterialDao;
//import de.elt.dispotool.entities.Material;
//import java.util.List;
//import java.util.Map;
//import javax.ejb.Stateful;
//import javax.faces.view.ViewScoped;
//import javax.inject.Inject;
//import org.primefaces.model.LazyDataModel;
//import org.primefaces.model.SortOrder;
//
///**
// *
// * @author jonas.eicher
// */
//@Stateful
////@ViewScoped
//public class LazyMaterialsModel extends LazyDataModel<Material> {
//
//  @Inject
//  MaterialDao materialDao;
//
//  public LazyMaterialsModel() {
//  }
//
//  @Override
//  public List<Material> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
//
//    return materialDao.load(first, pageSize, sortField, sortOrder, filters);
//
//  }
//
//  @Override
//  public int getRowCount() {
//    return materialDao.getRowCount();
//  }
//
//}
