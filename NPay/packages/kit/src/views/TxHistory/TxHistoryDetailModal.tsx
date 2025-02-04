import type { FC } from 'react';
import { useEffect } from 'react';

import { useRoute } from '@react-navigation/core';
import { useIntl } from 'react-intl';

import { Button, Modal, Spinner } from '@onekeyhq/components';
import type { TransactionDetailRoutesParams } from '@onekeyhq/kit/src/routes/Root/Modal/TransactionDetail';
import platformEnv from '@onekeyhq/shared/src/platformEnv';

import backgroundApiProxy from '../../background/instance/backgroundApiProxy';
import { TxActionElementTime } from '../TxDetail/elements/TxActionElementTime';
import { TxDetailView } from '../TxDetail/TxDetailView';

import type { TransactionDetailModalRoutes } from '../../routes/routesEnum';
import type { RouteProp } from '@react-navigation/native';

type TransactionDetailRouteProp = RouteProp<
  TransactionDetailRoutesParams,
  TransactionDetailModalRoutes.HistoryDetailModal
>;
const TxHistoryDetailModal: FC = () => {
  const route = useRoute<TransactionDetailRouteProp>();
  const intl = useIntl();
  const { decodedTx, historyTx } = route.params;
  useEffect(() => {
    if (!historyTx) {
      return;
    }
    const { accountId, networkId } = historyTx.decodedTx;
    const timer = setTimeout(() => {
      backgroundApiProxy.serviceHistory.updateHistoryStatus({
        networkId,
        accountId,
        items: [historyTx],
      });
      backgroundApiProxy.serviceHistory.updateHistoryFee({
        networkId,
        accountId,
        tx: historyTx,
      });
    }, 1500);
    return () => {
      clearTimeout(timer);
    };
  }, [historyTx]);

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const headerDescription = (
    <TxActionElementTime
      timestamp={decodedTx?.updatedAt ?? decodedTx?.createdAt}
    />
  );
  return (
    <Modal
      header={intl.formatMessage({ id: 'title__transaction_details' })}
      // headerDescription={headerDescription}
      footer={null}
      height="560px"
      scrollViewProps={{
        children: decodedTx ? (
          <>
            <TxDetailView
              isHistoryDetail
              decodedTx={decodedTx}
              historyTx={historyTx}
            />
            {platformEnv.isDev && (
              <Button
                testID="showTxDataBtn"
                mt={6}
                onPress={() => {
                  console.log({
                    decodedTx,
                    historyTx,
                  });
                }}
              >
                Show Tx Data
              </Button>
            )}
          </>
        ) : (
          <Spinner />
        ),
      }}
    />
  );
};

export { TxHistoryDetailModal };
