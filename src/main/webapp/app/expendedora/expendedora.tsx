import React, { useState, useEffect } from 'react';
import {getEntities} from 'app/entities/producto/producto.reducer';
import {calculadorCambio} from 'app/expendedora/expendedora.reducer';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { Button, Table } from 'reactstrap';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {ProductoSeleccionado} from './productoseleccionado';

export const Expendedora = () => {
	const dispatch = useAppDispatch();
	const productoList = useAppSelector(state => state.producto.entities);
	const loading = useAppSelector(state => state.producto.loading);
	const [curSelected,setCurSelected]= useState(null);
	const [curProduct, setCurProduct]= useState(null);
	const [cantidadAPagar, setCantidadAPagar]=useState(null);
	
	const onChangeCambio = (value) => {
		setCantidadAPagar(value);
		productoCambio="0";
	};
	const getAllEntities = () => {
	  dispatch(
	    getEntities({
	      page: 0,
	      size: 100,
	      //sort: SORT,ASC,
		  sort: `nombreProducto,${ASC}`,
	    }),
	  );
	};
	useEffect(() => {
	  getAllEntities();
	}, []);
	const descriptionProducto=curProduct!==null?curProduct?.nombreProducto+"-"+curProduct?.precio:"";
	const calcularCambio = values => {
		const entity={
			    "cantidadPagada": cantidadAPagar,
			    "clave" :curProduct.clave,
				"cambio": null,
			};
	    dispatch(calculadorCambio(entity));
	};
	let productoCambio = useAppSelector(state =>state.expendedora.entity);
	//const loading = true;
	return (
		<div>
		  <h2 id="producto-heading" data-cy="ProductoHeading">
		    Productos a pagar
		  </h2>
		  <div>
		  <input id="productName" value={descriptionProducto} disabled/>
		  
		  <input id="cantidadAPagar" value={curProduct===null?"":cantidadAPagar} onChange={e=>onChangeCambio(e.target.value)}/>
		  <Button className="me-2" color="info" onClick={calcularCambio} disabled={curSelected!==null&&cantidadAPagar!==null?false:true} >
		    <FontAwesomeIcon icon="sync" spin={loading} /> Pagar y obtener cambio
		  </Button>
		  <label>Tu cambio es $</label>
		  <input type="text" id="cambio" name="cambio" disabled value={cantidadAPagar==="0"?"":productoCambio?.cambio}/>
		  </div>
		  <div className="table-responsive">
		    {productoList && productoList.length > 0 ? (
		      <Table responsive>
		        <thead>
		          <tr>
		            <th className="hand">
		              Clave
		            </th>
		            <th className="hand">
		              Nombre Producto
		            </th>
		            <th className="hand">
		              Precio
		            </th>
		          </tr>
		        </thead>
		        <tbody>
		          {productoList.map((producto, i) => ( 
						<ProductoSeleccionado producto={producto} index={i} onSelected={setCurSelected} curSelected={curSelected} setCurProduct={setCurProduct} setCantidadAPagar={setCantidadAPagar}/>
              ))}
				  </tbody>
				</Table>
				) : (
				  !loading && <div className="alert alert-warning">Ningún Productos encontrado</div>
				)}
			</div>  
		</div>
	};

export default Expendedora;