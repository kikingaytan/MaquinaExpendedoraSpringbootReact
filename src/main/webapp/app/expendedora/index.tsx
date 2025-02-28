import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Expendedora from './expendedora';
import Producto from 'app/entities/producto';

const ExpendedoraRoutes = () =>(
	<ErrorBoundaryRoutes>
	<Route index element={<Expendedora/>}>
	</ErrorBoundaryRoutes>
);
export default ExpendedoraRoutes;