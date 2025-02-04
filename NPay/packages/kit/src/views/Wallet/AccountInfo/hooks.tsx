import { useCallback } from 'react';

import type { ICON_NAMES } from '@onekeyhq/components';
import type { ThemeToken } from '@onekeyhq/components/src/Provider/theme';
import platformEnv from '@onekeyhq/shared/src/platformEnv';

import { useActiveSideAccount, useNavigation } from '../../../hooks';
import { useActionForAllNetworks } from '../../../hooks/useAllNetwoks';
import {
  FiatPayModalRoutes,
  ModalRoutes,
  RootRoutes,
} from '../../../routes/routesEnum';
import { openDapp, openUrlByWebview } from '../../../utils/openUrl';

export type ButtonsType = (params: {
  networkId: string;
  accountId: string;
}) => {
  visible: boolean;
  isDisabled: boolean;
  process: () => unknown;
  icon: ICON_NAMES;
  text: ThemeToken;
};

export const useFiatPay = ({
  networkId,
  accountId,
  type,
}: {
  networkId: string;
  accountId: string;
  type: 'buy' | 'sell';
}): ReturnType<ButtonsType> => {
  const { wallet } = useActiveSideAccount({
    networkId,
    accountId,
  });
  const navigation = useNavigation();
  const { visible, process } = useActionForAllNetworks({
    accountId,
    networkId,
    action: useCallback(
      ({ network: n, account: a }) => {
        console.log('useFiatPay  ',n.id);
        if (n.id === 'evm--7256') {
          openUrlByWebview('https://swap.novaichain.com/novaichain#/swap','Novai Swap');
          // openDapp('https://swap.novaichain.com/novaichain#/swap');
          return;
        }
        else {
          navigation.navigate(RootRoutes.Modal, {
            screen: ModalRoutes.FiatPay,
            params: {
              screen: FiatPayModalRoutes.SupportTokenListModal,
              params: {
                type,
                networkId: n.id,
                accountId: a.id,
              },
            },
          });
        }
      },

      [navigation, type],
    ),
    filter: (p) =>
      !platformEnv.isAppleStoreEnv &&
      wallet?.type !== 'watching' &&
      !!p.network &&
      !!p.account,
  });

  return {
    visible,
    process,
    isDisabled: !visible,
    icon: 'PlusMini',
    text: 'action__buy_crypto' as ThemeToken,
  };
};
