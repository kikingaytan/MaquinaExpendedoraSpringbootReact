import { Button, Table } from 'reactstrap';
import React, { useState, useEffect } from 'react';

export const ProductoSeleccionado = ({ producto, index, onSelected, curSelected, setCurProduct, setCantidadAPagar }) => {
  const isSelected = index === curSelected;

  function handleOnSelected() {
    onSelected(isSelected ? null : index);
    setCurProduct(isSelected ? null : producto);
    setCantidadAPagar('0');
  }
  const classSelected = {
    color: 'green',
    backgroundcolor: 'indigo',
  };

  const classNotSelected = {
    color: 'black',
    backgroundcolor: 'blue',
  };

  return (
    <tr key={`entity-${index}`} data-cy="entityTable" onClick={handleOnSelected}>
      <td style={isSelected ? classSelected : classNotSelected}>{producto.clave}</td>
      <td style={isSelected ? classSelected : classNotSelected}>{producto.nombreProducto}</td>
      <td style={isSelected ? classSelected : classNotSelected}>{producto.precio}</td>
    </tr>
  );
};
export default ProductoSeleccionado;
