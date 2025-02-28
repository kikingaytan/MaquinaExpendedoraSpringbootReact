import React from 'react';

import { NavDropdown } from './menu-components';
import ExpendedoraItems from 'app/expendedora/menu';

export const ExpendedoraMenu = () => (
  <NavDropdown icon="btc" name="Expendedora" id="expendedora-menu" data-cy="expendedora" style={{ maxHeight: '80vh', overflow: 'auto' }}>
    <ExpendedoraItems />
  </NavDropdown>
);
export default ExpendedoraMenu;
