import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IProductoPagado, defaultValue } from 'app/shared/model/productopagadoI.model';

const initialState: EntityState<IProductoPagado> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  //cambio:"0";
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};
const apiUrl = 'api/productos';
const endpoint = 'calculadorCambio';

export const calculadorCambio = createAsyncThunk(
  'expendedora/calculadorCambio',
  async (entity: IProductoPagado, thunkAPI) => {
    const result = await axios.post<IProductoPagado>(`${apiUrl}/${endpoint}`, cleanEntity(entity));
    return result;
  },
  { serializeError: serializeAxiosError },
);
// slice

export const ExpendedoraSlice = createEntitySlice({
  name: 'expendedora',
  initialState,
  extraReducers(builder) {
    builder
      .addMatcher(isFulfilled(calculadorCambio), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
        console.log(action.payload.data);
      })
      .addMatcher(isPending(calculadorCambio), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = ExpendedoraSlice.actions;
// Reducer
export default ExpendedoraSlice.reducer;
